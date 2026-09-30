package com.example.myapplication.presentation.presentation.viewmodel.event

sealed interface InvoiceDetailEvent {
    data object MarkAsSent : InvoiceDetailEvent
    data object MarkAsPaid : InvoiceDetailEvent
    data object CancelInvoice : InvoiceDetailEvent
    data object Duplicate : InvoiceDetailEvent
    data object GeneratePdf : InvoiceDetailEvent   // Wirkung folgt in Phase 6
    data object DismissError : InvoiceDetailEvent
}