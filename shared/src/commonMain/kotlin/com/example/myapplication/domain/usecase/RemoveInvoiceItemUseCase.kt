package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.InvoiceStatus
import com.example.myapplication.domain.repository.InvoiceItemRepository
import com.example.myapplication.domain.repository.InvoiceRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.domain.results.ValidationErrorItem
import com.example.myapplication.util.AppClock

class RemoveInvoiceItemUseCase(
    private val invoiceRepository: InvoiceRepository,
    private val invoiceItemRepository: InvoiceItemRepository,
    private val calculateInvoiceTotals: CalculateInvoiceTotalsUseCase,
    private val clock: AppClock
) {
    suspend operator fun invoke(invoiceId: String, itemId: String, isSmallBusiness: Boolean): AppResult<Unit> {
        val invoice = invoiceRepository.getById(invoiceId)
            ?: return AppResult.Failure(NoSuchElementException("Rechnung nicht gefunden: $invoiceId"))

        if (invoice.status != InvoiceStatus.DRAFT) {
            return AppResult.ValidationError(
                listOf(
                    ValidationErrorItem(
                        "status",
                        "Positionen können nur bei Entwürfen entfernt werden."
                    )
                )
            )
        }

        return try {
            invoiceItemRepository.delete(itemId)

            // Positionen nach Löschung neu durchnummerieren, damit "position" lückenlos bleibt.
            val remainingItems = invoiceItemRepository.getForInvoice(invoiceId)
                .mapIndexed { index, item -> item.copy(position = index + 1) }
            remainingItems.forEach { invoiceItemRepository.update(it) }

            val totals = calculateInvoiceTotals(remainingItems, isSmallBusiness)
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
}