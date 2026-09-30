package com.example.myapplication.presentation.presentation.viewmodel.event

import com.example.myapplication.domain.model.InvoiceStatus

sealed interface InvoiceListEvent {
    data class SearchQueryChanged(val query: String) : InvoiceListEvent
    data class StatusFilterChanged(val status: InvoiceStatus?) : InvoiceListEvent
    data class DeleteDraftRequested(val invoiceId: String) : InvoiceListEvent
    data object DismissError : InvoiceListEvent
}