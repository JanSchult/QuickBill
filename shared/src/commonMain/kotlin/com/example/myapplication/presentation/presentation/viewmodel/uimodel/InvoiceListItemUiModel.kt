package com.example.myapplication.presentation.presentation.viewmodel.uimodel

import com.example.myapplication.domain.model.InvoiceStatus

data class InvoiceListItemUiModel(
    val id: String,
    val invoiceNumber: String,
    val customerDisplayName: String,
    val invoiceDateFormatted: String,
    val grossAmountFormatted: String,
    val status: InvoiceStatus
)