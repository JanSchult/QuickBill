package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.InvoiceItem
import com.example.myapplication.domain.model.InvoiceStatus
import com.example.myapplication.domain.model.ItemUnit
import com.example.myapplication.domain.model.Money
import com.example.myapplication.domain.model.TaxRate
import com.example.myapplication.domain.repository.InvoiceItemRepository
import com.example.myapplication.domain.repository.InvoiceRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.domain.results.ValidationErrorItem
import com.example.myapplication.util.AppClock
import com.example.myapplication.util.IdGenerator

class AddInvoiceItemUseCase(
    private val invoiceRepository: InvoiceRepository,
    private val invoiceItemRepository: InvoiceItemRepository,
    private val calculateInvoiceTotals: CalculateInvoiceTotalsUseCase,
    private val clock: AppClock
) {
    suspend operator fun invoke(
        invoiceId: String,
        description: String,
        quantityThousandths: Long,
        unit: ItemUnit,
        unitPriceNet: Money,
        discountBasisPoints: Int,
        taxRate: TaxRate,
        isSmallBusiness: Boolean
    ): AppResult<InvoiceItem> {
        val invoice = invoiceRepository.getById(invoiceId)
            ?: return AppResult.Failure(NoSuchElementException("Rechnung nicht gefunden: $invoiceId"))

        if (invoice.status != InvoiceStatus.DRAFT) {
            return AppResult.ValidationError(
                listOf(ValidationErrorItem("status", "Positionen können nur bei Entwürfen hinzugefügt werden."))
            )
        }

        if (discountBasisPoints !in 0..10_000) {
            return AppResult.ValidationError(
                listOf(
                    ValidationErrorItem(
                        "status",
                        "Positionen können nur bei Entwürfen hinzugefügt werden."
                    )
                )
            )
        }

        val existingItems = invoiceItemRepository.getForInvoice(invoiceId)
        val newItem = InvoiceItem(
            id = IdGenerator.newId(),
            invoiceId = invoiceId,
            position = existingItems.size + 1,
            description = description,
            quantityThousandths = quantityThousandths,
            unit = unit,
            unitPriceNet = unitPriceNet,
            discountBasisPoints = discountBasisPoints,
            taxRate = taxRate,
            netAmount = Money.ZERO,
            taxAmount = Money.ZERO,
            grossAmount = Money.ZERO
        )

        return try {
            invoiceItemRepository.insert(newItem)
            recalculateAndPersistTotals(invoiceId, isSmallBusiness)
            val persisted = invoiceItemRepository.getForInvoice(invoiceId).first { it.id == newItem.id }
            AppResult.Success(persisted)
        } catch (e: Exception) {
            AppResult.Failure(e)
        }
    }

    private suspend fun recalculateAndPersistTotals(invoiceId: String, isSmallBusiness: Boolean) {
        val invoice = invoiceRepository.getById(invoiceId) ?: return
        val items = invoiceItemRepository.getForInvoice(invoiceId)
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
    }


}