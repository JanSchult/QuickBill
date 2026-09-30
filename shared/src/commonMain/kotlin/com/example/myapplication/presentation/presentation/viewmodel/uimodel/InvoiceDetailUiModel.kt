package com.example.myapplication.presentation.presentation.viewmodel.uimodel

import com.example.myapplication.domain.model.InvoiceStatus

data class InvoiceDetailUiModel(
    val id: String,
    val invoiceNumber: String,
    val status: InvoiceStatus,
    val customerDisplayName: String,
    val invoiceDateDisplay: String,
    val dueDateDisplay: String,
    val items: List<InvoiceDetailItemUiModel>,
    val netAmountFormatted: String,
    val taxAmountFormatted: String,
    val grossAmountFormatted: String,
    val note: String,
    val paymentTerms: String,
    val pdfFileReference: String?
)