package com.example.myapplication.domain.pdf

data class PdfTaxBreakdownRow(
    val taxRateDisplay: String,
    val netAmountFormatted: String,
    val taxAmountFormatted: String
)