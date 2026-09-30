package com.example.myapplication.presentation.presentation.viewmodel.uimodel

data class SettingsFormUiModel(
    val invoiceNumberPrefix: String,
    val defaultPaymentTermDays: String,
    val defaultInvoiceNote: String,
    val smallBusinessNoticeText: String,
    val pdfLayoutReference: String?
)