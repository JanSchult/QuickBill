package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.repository.InvoiceItemRepository
import com.example.myapplication.domain.repository.InvoiceRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.domain.results.ValidationErrorItem

class DeleteInvoiceDraftUseCase(
    private val invoiceRepository: InvoiceRepository,
    private val invoiceItemRepository: InvoiceItemRepository
) {
    suspend operator fun invoke(invoiceId: String): AppResult<Unit> {
        return try {
            invoiceItemRepository.deleteAllForInvoice(invoiceId)
            val wasDeleted = invoiceRepository.deleteDraft(invoiceId)
            if (!wasDeleted) {
                return AppResult.ValidationError(
                    listOf(ValidationErrorItem("status", "Nur Entwürfe können gelöscht werden."))
                )
            }
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Failure(e)
        }
    }
}