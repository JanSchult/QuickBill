package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Invoice
import com.example.myapplication.domain.model.InvoiceStatus
import com.example.myapplication.domain.repository.InvoiceRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.domain.results.ValidationErrorItem
import com.example.myapplication.util.AppClock
import kotlinx.datetime.LocalDate

/**
 * Aktualisiert die Kopfdaten eines Entwurfs (Kunde, Daten, Notizen).
 * In der ursprünglichen Use-Case-Liste fehlend - CreateInvoiceDraftUseCase deckt
 * nur die Erstellung ab, der Editor-Screen braucht aber auch Bearbeitung.
 */
class UpdateInvoiceDraftUseCase(
    private val invoiceRepository: InvoiceRepository,
    private val clock: AppClock
) {
    suspend operator fun invoke(
        invoiceId: String,
        customerId: String?,
        invoiceDate: LocalDate,
        dueDate: LocalDate,
        serviceDate: LocalDate?,
        servicePeriodFrom: LocalDate? = null,
        servicePeriodTo: LocalDate? = null,
        paymentTerms: String,
        note: String,
        additionalHint: String
    ): AppResult<Invoice> {
        val invoice = invoiceRepository.getById(invoiceId)
            ?: return AppResult.Failure(NoSuchElementException("Rechnung nicht gefunden: $invoiceId"))

        if (invoice.status != InvoiceStatus.DRAFT) {
            return AppResult.ValidationError(
                listOf(ValidationErrorItem("status", "Nur Entwürfe können bearbeitet werden."))
            )
        }

        val errors = mutableListOf<ValidationErrorItem>()
        if (dueDate < invoiceDate) {
            errors += ValidationErrorItem(
                "dueDate",
                "Fälligkeitsdatum darf nicht vor dem Rechnungsdatum liegen."
            )
        }
        if (servicePeriodFrom != null && servicePeriodTo != null && servicePeriodTo < servicePeriodFrom) {
            errors += ValidationErrorItem("servicePeriod", "Leistungszeitraum-Ende darf nicht vor dem Beginn liegen.")
        }
        if (errors.isNotEmpty()) return AppResult.ValidationError(errors)

        val updated = invoice.copy(
            customerId = customerId,
            invoiceDate = invoiceDate,
            dueDate = dueDate,
            serviceDate = serviceDate,
            servicePeriodFrom = servicePeriodFrom,
            servicePeriodTo = servicePeriodTo,
            paymentTerms = paymentTerms,
            note = note,
            additionalHint = additionalHint,
            updatedAt = clock.nowDateTime()
        )

        return try {
            invoiceRepository.update(updated)
            AppResult.Success(updated)
        } catch (e: Exception) {
            AppResult.Failure(e)
        }
    }
}