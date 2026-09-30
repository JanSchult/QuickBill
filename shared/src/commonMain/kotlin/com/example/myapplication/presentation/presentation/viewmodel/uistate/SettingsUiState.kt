package com.example.myapplication.presentation.presentation.viewmodel.uistate

import com.example.myapplication.presentation.presentation.viewmodel.uimodel.SettingsFormUiModel

data class SettingsUiState(
    val isLoading: Boolean = true,
    val form: SettingsFormUiModel? = null,
    val activeCompanyName: String? = null,
    val validationErrors: Map<String, String> = emptyMap(),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)