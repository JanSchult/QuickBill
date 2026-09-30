package com.example.myapplication.presentation.presentation.viewmodel.uistate

import com.example.myapplication.domain.model.InvoiceStatus
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.InvoiceListItemUiModel

data class InvoiceListUiState(
    val isLoading: Boolean = true,
    val invoices: List<InvoiceListItemUiModel> = emptyList(),
    val searchQuery: String = "",
    val statusFilter: InvoiceStatus? = null,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean get() = !isLoading && invoices.isEmpty() && errorMessage == null
}