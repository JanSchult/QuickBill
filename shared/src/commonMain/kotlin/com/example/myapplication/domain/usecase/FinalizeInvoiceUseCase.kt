package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Invoice
import com.example.myapplication.domain.model.InvoiceItem
import com.example.myapplication.domain.model.InvoiceStatus
import com.example.myapplication.domain.repository.InvoiceItemRepository
import com.example.myapplication.domain.repository.InvoiceRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.domain.results.ValidationErrorItem
import com.example.myapplication.util.AppClock

/**
 * Finalisiert einen Entwurf: prüft Pflichtfelder, berechnet die Summen ein letztes Mal,
 * vergibt die endgültige Rechnungsnummer und setzt den Status auf OPEN.
 * Ab hier gelten Positionen als unveränderlich (siehe Status-Prüfungen oben).
 */
class FinalizeInvoiceUseCase(
    private val invoiceRepository: InvoiceRepository,
    private val invoiceItemRepository: InvoiceItemRepository,
    private val generateInvoiceNumber: GenerateInvoiceNumberUseCase,
    private val calculateInvoiceTotals: CalculateInvoiceTotalsUseCase,
    private val clock: AppClock
) {
    suspend operator fun invoke(
        invoiceId: String,
        invoiceNumberPrefix: String,
        isSmallBusiness: Boolean
    ): AppResult<Invoice> {
        val invoice = invoiceRepository.getById(invoiceId)
            ?: return AppResult.Failure(NoSuchElementException("Rechnung nicht gefunden: $invoiceId"))

        if (invoice.status != InvoiceStatus.DRAFT) {
            return AppResult.ValidationError(
                listOf(ValidationErrorItem("status", "Nur Entwürfe können finalisiert werden."))
            )
        }

        val items = invoiceItemRepository.getForInvoice(invoiceId)
        val errors = validate(invoice, items)
        if (errors.isNotEmpty()) return AppResult.ValidationError(errors)

        return try {
            val totals = calculateInvoiceTotals(items, isSmallBusiness)
            totals.items.forEach { invoiceItemRepository.update(it) }

            val invoiceNumber = generateInvoiceNumber(
                companyId = invoice.companyId,
                prefix = invoiceNumberPrefix,
                year = invoice.invoiceDate.year
            )

            val finalized = invoice.copy(
                invoiceNumber = invoiceNumber,
                status = InvoiceStatus.OPEN,
                netAmount = totals.totalNet,
                taxAmount = totals.totalTax,
                grossAmount = totals.totalGross,
                updatedAt = clock.nowDateTime()
            )
            invoiceRepository.update(finalized)
            AppResult.Success(finalized)
        } catch (e: Exception) {
            AppResult.Failure(e)
        }
    }

    private fun validate(invoice: Invoice, items: List<InvoiceItem>): List<ValidationErrorItem> {
        val errors = mutableListOf<ValidationErrorItem>()
        if (invoice.customerId == null) {   // ## ergänzt: Kunde ist erst bei Finalisierung Pflicht
            errors += ValidationErrorItem("customerId", "Bitte einen Kunden auswählen.")
        }
        if (items.isEmpty()) errors += ValidationErrorItem("items", "Eine Rechnung benötigt mindestens eine Position.")
        if (invoice.dueDate < invoice.invoiceDate) {
            errors += ValidationErrorItem("dueDate", "Fälligkeitsdatum darf nicht vor dem Rechnungsdatum liegen.")
        }
        return errors
    }
}