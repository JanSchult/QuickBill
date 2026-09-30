package com.example.myapplication.domain.model


data class InvoiceItem(
    val id: String,
    val invoiceId: String,
    val position: Int,
    val description: String,
    val quantityThousandths: Long,
    val unit: ItemUnit,
    val unitPriceNet: Money,
    val discountBasisPoints: Int,
    val taxRate: TaxRate,
    val netAmount: Money,
    val taxAmount: Money,
    val grossAmount: Money
)