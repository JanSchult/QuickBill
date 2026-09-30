package com.example.myapplication.domain.model


data class AppSettings(
    val activeCompanyId: String?,
    val invoiceNumberPrefix: String,
    val defaultPaymentTermDays: Int,
    val defaultInvoiceNote: String,
    val smallBusinessNoticeText: String,
    val pdfLayoutReference: String?
)