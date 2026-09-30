package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Invoice
import com.example.myapplication.domain.model.InvoiceStatus
import com.example.myapplication.domain.model.Money
import com.example.myapplication.domain.repository.InvoiceRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.domain.results.ValidationErrorItem
import com.example.myapplication.util.AppClock
import com.example.myapplication.util.IdGenerator
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

class CreateInvoiceDraftUseCase(
    private val invoiceRepository: InvoiceRepository,
    private val clock: AppClock
) {
    suspend operator fun invoke(
        companyId: String,
        customerId: String? = null,
        invoiceDate: LocalDate,
        paymentTermDays: Int,
        currency: String,
        paymentTerms: String,
        note: String = "",
        additionalHint: String = ""
    ): AppResult<Invoice> {
        if (paymentTermDays < 0) {
            return AppResult.ValidationError(
                listOf(
                    ValidationErrorItem(
                        "paymentTermDays",
                        "Zahlungsziel darf nicht negativ sein."
                    )
                )
            )
        }

        val now = clock.nowDateTime()
        val invoice = Invoice(
            id = IdGenerator.newId(),
            companyId = companyId,
            customerId = customerId,
            // Entwürfe erhalten eine temporäre Kennung - die endgültige Nummer wird
            // erst bei FinalizeInvoiceUseCase vergeben (Phase 7-Vorgabe).
            invoiceNumber = "ENTWURF-${IdGenerator.newId().take(8)}",
            invoiceDate = invoiceDate,
            serviceDate = null,
            servicePeriodFrom = null,
            servicePeriodTo = null,
            dueDate = invoiceDate.plus(DatePeriod(days = paymentTermDays)),
            status = InvoiceStatus.DRAFT,
            currency = currency,
            note = note,
            paymentTerms = paymentTerms,
            additionalHint = additionalHint,
            netAmount = Money.ZERO,
            taxAmount = Money.ZERO,
            grossAmount = Money.ZERO,
            createdAt = now,
            updatedAt = now,
            paidAt = null,
            cancelledAt = null,
            pdfFileReference = null
        )

        return try {
            invoiceRepository.insert(invoice)
            AppResult.Success(invoice)
        } catch (e: Exception) {
            AppResult.Failure(e)
        }
    }
}