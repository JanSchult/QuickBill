package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Invoice
import com.example.myapplication.domain.model.InvoiceStatus
import com.example.myapplication.domain.repository.InvoiceRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.domain.results.ValidationErrorItem
import com.example.myapplication.util.AppClock

/**
 * Storniert eine Rechnung, statt sie zu löschen. Die Rechnungsnummer wird nicht
 * erneut vergeben (Nachvollziehbarkeit, Phase 7-Vorgabe).
 */
class CancelInvoiceUseCase(
    private val invoiceRepository: InvoiceRepository,
    private val clock: AppClock
) {
    private val nonCancellableStatuses = setOf(InvoiceStatus.CANCELLED, InvoiceStatus.PAID)

    suspend operator fun invoke(invoiceId: String): AppResult<Invoice> {
        val invoice = invoiceRepository.getById(invoiceId)
            ?: return AppResult.Failure(NoSuchElementException("Rechnung nicht gefunden: $invoiceId"))

        if (invoice.status in nonCancellableStatuses) {
            return AppResult.ValidationError(
                listOf(
                    ValidationErrorItem(
                        "status",
                        "Bezahlte oder bereits stornierte Rechnungen können nicht storniert werden."
                    )
                )
            )
        }

        val now = clock.nowDateTime()
        val updated = invoice.copy(status = InvoiceStatus.CANCELLED, cancelledAt = now, updatedAt = now)
        return try {
            invoiceRepository.update(updated)
            AppResult.Success(updated)
        } catch (e: Exception) {
            AppResult.Failure(e)
        }
    }
}