package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.InvoiceItem
import com.example.myapplication.domain.model.InvoiceStatus
import com.example.myapplication.domain.repository.InvoiceItemRepository
import com.example.myapplication.domain.repository.InvoiceRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.domain.results.ValidationErrorItem
import com.example.myapplication.util.AppClock

class UpdateInvoiceItemUseCase(
    private val invoiceRepository: InvoiceRepository,
    private val invoiceItemRepository: InvoiceItemRepository,
    private val calculateInvoiceTotals: CalculateInvoiceTotalsUseCase,
    private val clock: AppClock
) {
    suspend operator fun invoke(item: InvoiceItem, isSmallBusiness: Boolean): AppResult<Unit> {
        val invoice = invoiceRepository.getById(item.invoiceId)
            ?: return AppResult.Failure(NoSuchElementException("Rechnung nicht gefunden: ${item.invoiceId}"))

        if (invoice.status != InvoiceStatus.DRAFT) {
            return AppResult.ValidationError(
                listOf(
                    ValidationErrorItem(
                        "status",
                        "Positionen können nur bei Entwürfen bearbeitet werden."
                    )
                )
            )
        }

        val errors = validate(item)
        if (errors.isNotEmpty()) return AppResult.ValidationError(errors)

        return try {
            invoiceItemRepository.update(item)

            val items = invoiceItemRepository.getForInvoice(item.invoiceId)
            val totals = calculateInvoiceTotals(items, isSmallBusiness)
            totals.items.forEach { invoiceItemRepository.update(it) }

            invoiceRepository.update(
                invoice.copy(
                    netAmount = totals.totalNet,
                    taxAmount = totals.totalTax,
                    grossAmount = totals.totalGross,
                    updatedAt = clock.nowDateTime()
                )
            )
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Failure(e)
        }
    }

    private fun validate(item: InvoiceItem): List<ValidationErrorItem> {
        val errors = mutableListOf<ValidationErrorItem>()
        if (item.description.isBlank()) errors += ValidationErrorItem("description", "Beschreibung darf nicht leer sein.")
        if (item.quantityThousandths <= 0) errors += ValidationErrorItem("quantity", "Menge muss größer als 0 sein.")
        if (item.unitPriceNet.isNegative()) errors += ValidationErrorItem("unitPriceNet", "Einzelpreis darf nicht negativ sein.")
        return errors
    }
}