package com.example.myapplication.presentation.presentation.viewmodel.uimodel

data class CustomerHistoryInvoiceUiModel(
    val id: String,
    val invoiceNumber: String,
    val statusDisplay: String,
    val grossAmountFormatted: String
)