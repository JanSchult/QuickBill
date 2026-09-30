package com.example.myapplication.presentation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.AppSettings
import com.example.myapplication.domain.repository.AppSettingsRepository
import com.example.myapplication.domain.repository.CompanyRepository
import com.example.myapplication.domain.usecase.SaveAppSettingsUseCase
import com.example.myapplication.presentation.presentation.viewmodel.event.SettingsEvent
import com.example.myapplication.presentation.presentation.viewmodel.uistate.SettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.SettingsFormUiModel
import com.example.myapplication.domain.results.AppResult

class SettingsViewModel(
    private val appSettingsRepository: AppSettingsRepository,
    private val companyRepository: CompanyRepository,
    private val saveAppSettings: SaveAppSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var lastKnownSettings: AppSettings? = null

    init {
        appSettingsRepository.observe()
            .onEach { settings -> applySettings(settings) }
            .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Unbekannter Fehler") } }
            .launchIn(viewModelScope)
    }

    private suspend fun applySettings(settings: AppSettings?) {
        lastKnownSettings = settings
        val companyName = settings?.activeCompanyId?.let { companyRepository.getCompany(it)?.name }
        _uiState.update { it.copy(isLoading = false, activeCompanyName = companyName, form = settings?.toFormUiModel() ?: defaultForm()) }
    }

    fun onEvent(event: SettingsEvent) {
        val form = _uiState.value.form ?: return
        when (event) {
            is SettingsEvent.InvoiceNumberPrefixChanged -> updateForm(form.copy(invoiceNumberPrefix = event.value))
            is SettingsEvent.DefaultPaymentTermDaysChanged -> updateForm(form.copy(defaultPaymentTermDays = event.value))
            is SettingsEvent.DefaultInvoiceNoteChanged -> updateForm(form.copy(defaultInvoiceNote = event.value))
            is SettingsEvent.SmallBusinessNoticeTextChanged -> updateForm(form.copy(smallBusinessNoticeText = event.value))
            is SettingsEvent.Save -> onSave(form)
            is SettingsEvent.DismissError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun updateForm(newForm: SettingsFormUiModel) {
        _uiState.update { it.copy(form = newForm, isSaved = false) }
    }

    private fun onSave(form: SettingsFormUiModel) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val settings = AppSettings(
                activeCompanyId = lastKnownSettings?.activeCompanyId,
                invoiceNumberPrefix = form.invoiceNumberPrefix,
                defaultPaymentTermDays = form.defaultPaymentTermDays.toIntOrNull() ?: 14,
                defaultInvoiceNote = form.defaultInvoiceNote,
                smallBusinessNoticeText = form.smallBusinessNoticeText,
                pdfLayoutReference = form.pdfLayoutReference
            )
            when (val result = saveAppSettings(settings)) {
                is AppResult.Success -> _uiState.update { it.copy(isSaving = false, isSaved = true, validationErrors = emptyMap()) }
                is AppResult.ValidationError -> _uiState.update {
                    it.copy(isSaving = false, validationErrors = result.errors.associate { e -> e.field to e.message })
                }
                is AppResult.Failure -> _uiState.update { it.copy(isSaving = false, errorMessage = result.cause.message) }
            }
        }
    }

    private fun defaultForm() = SettingsFormUiModel(
        invoiceNumberPrefix = "RE", defaultPaymentTermDays = "14", defaultInvoiceNote = "",
        smallBusinessNoticeText = "Gemäß § 19 UStG wird keine Umsatzsteuer berechnet.", pdfLayoutReference = null
    )

    private fun AppSettings.toFormUiModel() = SettingsFormUiModel(
        invoiceNumberPrefix = invoiceNumberPrefix, defaultPaymentTermDays = defaultPaymentTermDays.toString(),
        defaultInvoiceNote = defaultInvoiceNote, smallBusinessNoticeText = smallBusinessNoticeText,
        pdfLayoutReference = pdfLayoutReference
    )
}