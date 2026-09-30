package com.example.myapplication.presentation.presentation.viewmodel.uistate

import com.example.myapplication.domain.model.CustomerSortOrder
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.CustomerListItemUiModel

data class CustomerListUiState(
    val isLoading: Boolean = true,
    val customers: List<CustomerListItemUiModel> = emptyList(),
    val searchQuery: String = "",
    val sortOrder: CustomerSortOrder = CustomerSortOrder.NAME,
    val showArchived: Boolean = false,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean get() = !isLoading && customers.isEmpty() && errorMessage == null
}