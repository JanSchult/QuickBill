package com.example.myapplication.domain.pdf

data class PdfInvoiceItemRow(
    val position: Int,
    val description: String,
    val quantityAndUnitDisplay: String,
    val unitPriceFormatted: String,
    val taxRateDisplay: String,
    val netAmountFormatted: String
)
