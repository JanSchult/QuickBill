package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Invoice
import com.example.myapplication.domain.model.InvoiceStatus
import com.example.myapplication.domain.repository.InvoiceItemRepository
import com.example.myapplication.domain.repository.InvoiceRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.util.AppClock
import com.example.myapplication.util.IdGenerator
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus

/**
 * Dupliziert eine Rechnung als neuen Entwurf (inkl. Positionen), mit aktuellem Datum
 * und zurückgesetztem Zahlungs-/Stornostatus. Die Kopie erhält bei Finalisierung eine
 * eigene, neue Rechnungsnummer - nie die des Originals.
 */
class DuplicateInvoiceUseCase(
    private val invoiceRepository: InvoiceRepository,
    private val invoiceItemRepository: InvoiceItemRepository,
    private val clock: AppClock
) {
    suspend operator fun invoke(sourceInvoiceId: String): AppResult<Invoice> {
        val source = invoiceRepository.getById(sourceInvoiceId)
            ?: return AppResult.Failure(NoSuchElementException("Rechnung nicht gefunden: $sourceInvoiceId"))
        val sourceItems = invoiceItemRepository.getForInvoice(sourceInvoiceId)

        val now = clock.nowDateTime()
        val today = clock.today()
        val paymentTermDays = source.invoiceDate.daysUntil(source.dueDate).coerceAtLeast(0)

        val newInvoice = source.copy(
            id = IdGenerator.newId(),
            invoiceNumber = "ENTWURF-${IdGenerator.newId().take(8)}",
            invoiceDate = today,
            dueDate = today.plus(DatePeriod(days = paymentTermDays)),
            status = InvoiceStatus.DRAFT,
            createdAt = now,
            updatedAt = now,
            paidAt = null,
            cancelledAt = null,
            pdfFileReference = null
        )

        return try {
            invoiceRepository.insert(newInvoice)
            sourceItems.forEach { item ->
                invoiceItemRepository.insert(item.copy(id = IdGenerator.newId(), invoiceId = newInvoice.id))
            }
            AppResult.Success(newInvoice)
        } catch (e: Exception) {
            AppResult.Failure(e)
        }
    }
}