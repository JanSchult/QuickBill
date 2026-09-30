package com.example.myapplication.presentation.presentation.viewmodel.uimodel

import com.example.myapplication.domain.model.ItemUnit

data class InvoiceItemUiModel(
    val id: String,
    val position: Int,
    val description: String,
    val quantityDisplay: String,
    val unit: ItemUnit,
    val unitPriceDisplay: String,
    val discountDisplay: String,
    val taxRateDisplay: String,
    val netAmountFormatted: String,
    val taxAmountFormatted: String,
    val grossAmountFormatted: String
)