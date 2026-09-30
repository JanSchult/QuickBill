package com.example.myapplication.presentation.presentation.viewmodel.uistate

import com.example.myapplication.presentation.presentation.viewmodel.uimodel.CompanyProfileFormUiModel

data class CompanyProfileUiState(
    val isLoading: Boolean = true,
    val form: CompanyProfileFormUiModel? = null,
    val validationErrors: Map<String, String> = emptyMap(),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)