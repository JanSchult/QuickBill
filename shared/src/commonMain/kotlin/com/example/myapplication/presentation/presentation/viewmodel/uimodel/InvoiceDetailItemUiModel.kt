package com.example.myapplication.presentation.presentation.viewmodel.uimodel

data class InvoiceDetailItemUiModel(
    val description: String,
    val quantityAndUnitDisplay: String,
    val unitPriceDisplay: String,
    val taxRateDisplay: String,
    val netAmountFormatted: String,
    val taxAmountFormatted: String,
    val grossAmountFormatted: String
)