package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Invoice
import com.example.myapplication.domain.model.InvoiceStatus
import com.example.myapplication.domain.repository.InvoiceRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.domain.results.ValidationErrorItem
import com.example.myapplication.util.AppClock

class MarkInvoiceAsSentUseCase(
    private val invoiceRepository: InvoiceRepository,
    private val clock: AppClock
) {
    suspend operator fun invoke(invoiceId: String): AppResult<Invoice> {
        val invoice = invoiceRepository.getById(invoiceId)
            ?: return AppResult.Failure(NoSuchElementException("Rechnung nicht gefunden: $invoiceId"))

        if (invoice.status != InvoiceStatus.OPEN) {
            return AppResult.ValidationError(
                listOf(
                    ValidationErrorItem(
                        "status",
                        "Nur offene Rechnungen können als versendet markiert werden."
                    )
                )
            )
        }

        val updated = invoice.copy(status = InvoiceStatus.SENT, updatedAt = clock.nowDateTime())
        return try {
            invoiceRepository.update(updated)
            AppResult.Success(updated)
        } catch (e: Exception) {
            AppResult.Failure(e)
        }
    }
}