package com.example.myapplication.presentation.presentation.viewmodel.uimodel

import com.example.myapplication.domain.model.InvoiceStatus
import kotlinx.datetime.LocalDate

data class InvoiceDraftUiModel(
    val id: String,
    val status: InvoiceStatus,
    val invoiceNumber: String,
    val customerId: String?,
    val invoiceDate: LocalDate,              // ## ergänzt
    val invoiceDateDisplay: String,
    val serviceDate: LocalDate?,             // ## ergänzt
    val serviceDateDisplay: String,
    val dueDate: LocalDate,                  // ## ergänzt
    val dueDateDisplay: String,
    val paymentTerms: String,
    val note: String,
    val additionalHint: String,
    val items: List<InvoiceItemUiModel>,
    val netAmountFormatted: String,
    val taxAmountFormatted: String,
    val grossAmountFormatted: String,
    val isSmallBusinessNoticeShown: Boolean
)