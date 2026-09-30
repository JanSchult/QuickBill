package com.example.myapplication.presentation.presentation.viewmodel.uistate

import com.example.myapplication.presentation.presentation.viewmodel.uimodel.InvoiceDetailUiModel

data class InvoiceDetailUiState(
    val isLoading: Boolean = true,
    val invoice: InvoiceDetailUiModel? = null,
    val errorMessage: String? = null,
    val isProcessingAction: Boolean = false,
    val duplicatedInvoiceId: String? = null   // signalisiert dem Screen die Navigation zum neuen Entwurf
)