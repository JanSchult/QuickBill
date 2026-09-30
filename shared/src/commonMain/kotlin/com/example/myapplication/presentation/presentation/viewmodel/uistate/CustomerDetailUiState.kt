package com.example.myapplication.presentation.presentation.viewmodel.uistate

import com.example.myapplication.presentation.presentation.viewmodel.uimodel.CustomerFormUiModel
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.CustomerHistoryInvoiceUiModel

data class CustomerDetailUiState(
    val isLoading: Boolean = true,
    val isNewCustomer: Boolean = false,
    val form: CustomerFormUiModel? = null,
    val openInvoices: List<CustomerHistoryInvoiceUiModel> = emptyList(),
    val totalRevenueFormatted: String = "0,00 €",
    val validationErrors: Map<String, String> = emptyMap(),
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val duplicatedCustomerId: String? = null
)