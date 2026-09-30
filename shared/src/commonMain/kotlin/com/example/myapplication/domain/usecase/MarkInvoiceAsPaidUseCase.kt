package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Invoice
import com.example.myapplication.domain.model.InvoiceStatus
import com.example.myapplication.domain.repository.InvoiceRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.domain.results.ValidationErrorItem
import com.example.myapplication.util.AppClock

class MarkInvoiceAsPaidUseCase(
    private val invoiceRepository: InvoiceRepository,
    private val clock: AppClock
) {
    private val allowedSourceStatuses = setOf(
        InvoiceStatus.OPEN, InvoiceStatus.SENT, InvoiceStatus.PARTIALLY_PAID, InvoiceStatus.OVERDUE
    )

    suspend operator fun invoke(invoiceId: String): AppResult<Invoice> {
        val invoice = invoiceRepository.getById(invoiceId)
            ?: return AppResult.Failure(NoSuchElementException("Rechnung nicht gefunden: $invoiceId"))

        if (invoice.status !in allowedSourceStatuses) {
            return AppResult.ValidationError(
                listOf(
                    ValidationErrorItem(
                        "status",
                        "Rechnung kann aus dem aktuellen Status nicht als bezahlt markiert werden."
                    )
                )
            )
        }

        val now = clock.nowDateTime()
        val updated = invoice.copy(status = InvoiceStatus.PAID, paidAt = now, updatedAt = now)
        return try {
            invoiceRepository.update(updated)
            AppResult.Success(updated)
        } catch (e: Exception) {
            AppResult.Failure(e)
        }
    }
}