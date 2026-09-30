package com.example.myapplication.presentation.presentation.viewmodel.uistate

import com.example.myapplication.presentation.presentation.viewmodel.uimodel.CustomerUiModel
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.InvoiceDraftUiModel

data class InvoiceEditorUiState(
    val isLoading: Boolean = true,
    val invoice: InvoiceDraftUiModel? = null,
    val customers: List<CustomerUiModel> = emptyList(),
    val validationErrors: Map<String, String> = emptyMap(),
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    val isFinalized: Boolean = false
)