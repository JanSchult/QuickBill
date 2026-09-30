package com.example.myapplication.presentation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.AppSettings
import com.example.myapplication.domain.model.Company
import com.example.myapplication.domain.repository.AppSettingsRepository
import com.example.myapplication.domain.repository.CompanyRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.domain.usecase.SaveCompanyProfileUseCase
import com.example.myapplication.presentation.presentation.viewmodel.event.CompanyProfileEvent
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.CompanyProfileFormUiModel
import com.example.myapplication.presentation.presentation.viewmodel.uistate.CompanyProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** companyId == null bedeutet Erstanlage. */
class CompanyProfileViewModel(
    private val companyId: String?,
    private val companyRepository: CompanyRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val saveCompanyProfile: SaveCompanyProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CompanyProfileUiState(isLoading = companyId != null, form = if (companyId == null) emptyForm() else null)
    )
    val uiState: StateFlow<CompanyProfileUiState> = _uiState.asStateFlow()

    init {
        if (companyId != null) loadCompany(companyId)
    }

    private fun loadCompany(id: String) {
        viewModelScope.launch {
            val company = companyRepository.getCompany(id)
            if (company == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Unternehmensprofil nicht gefunden.") }
                return@launch
            }
            _uiState.update { it.copy(isLoading = false, form = company.toFormUiModel()) }
        }
    }

    fun onEvent(event: CompanyProfileEvent) {
        val form = _uiState.value.form ?: return
        when (event) {
            is CompanyProfileEvent.NameChanged -> updateForm(form.copy(name = event.value))
            is CompanyProfileEvent.StreetChanged -> updateForm(form.copy(street = event.value))
            is CompanyProfileEvent.HouseNumberChanged -> updateForm(form.copy(houseNumber = event.value))
            is CompanyProfileEvent.PostalCodeChanged -> updateForm(form.copy(postalCode = event.value))
            is CompanyProfileEvent.CityChanged -> updateForm(form.copy(city = event.value))
            is CompanyProfileEvent.CountryChanged -> updateForm(form.copy(country = event.value))
            is CompanyProfileEvent.EmailChanged -> updateForm(form.copy(email = event.value))
            is CompanyProfileEvent.PhoneChanged -> updateForm(form.copy(phone = event.value))
            is CompanyProfileEvent.WebsiteChanged -> updateForm(form.copy(website = event.value))
            is CompanyProfileEvent.TaxNumberChanged -> updateForm(form.copy(taxNumber = event.value))
            is CompanyProfileEvent.VatIdChanged -> updateForm(form.copy(vatId = event.value))
            is CompanyProfileEvent.BankNameChanged -> updateForm(form.copy(bankName = event.value))
            is CompanyProfileEvent.IbanChanged -> updateForm(form.copy(iban = event.value))
            is CompanyProfileEvent.BicChanged -> updateForm(form.copy(bic = event.value))
            is CompanyProfileEvent.IsSmallBusinessChanged -> updateForm(form.copy(isSmallBusiness = event.value))
            is CompanyProfileEvent.DefaultCurrencyChanged -> updateForm(form.copy(defaultCurrency = event.value))
            is CompanyProfileEvent.DefaultPaymentTermDaysChanged -> updateForm(form.copy(defaultPaymentTermDays = event.value))
            is CompanyProfileEvent.DefaultInvoiceNoteChanged -> updateForm(form.copy(defaultInvoiceNote = event.value))
            is CompanyProfileEvent.LogoChanged -> updateForm(form.copy(logo = event.bytes))
            is CompanyProfileEvent.Save -> onSave(form)
            is CompanyProfileEvent.DismissError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun updateForm(newForm: CompanyProfileFormUiModel) {
        _uiState.update { it.copy(form = newForm, isSaved = false) }
    }

    private fun onSave(form: CompanyProfileFormUiModel) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val result = saveCompanyProfile(
                existingId = form.id, name = form.name, street = form.street, houseNumber = form.houseNumber,
                postalCode = form.postalCode, city = form.city, country = form.country, email = form.email,
                phone = form.phone, website = form.website, taxNumber = form.taxNumber, vatId = form.vatId,
                bankName = form.bankName, iban = form.iban, bic = form.bic, isSmallBusiness = form.isSmallBusiness,
                defaultCurrency = form.defaultCurrency,
                defaultPaymentTermDays = form.defaultPaymentTermDays.toIntOrNull() ?: 14,
                defaultInvoiceNote = form.defaultInvoiceNote, logo = form.logo
            )

            when (result) {
                is AppResult.Success -> {
                    ensureActiveCompany(result.data)
                    _uiState.update { it.copy(isSaving = false, isSaved = true, validationErrors = emptyMap(), form = result.data.toFormUiModel()) }
                }
                is AppResult.ValidationError -> _uiState.update {
                    it.copy(isSaving = false, validationErrors = result.errors.associate { e -> e.field to e.message })
                }
                is AppResult.Failure -> _uiState.update { it.copy(isSaving = false, errorMessage = result.cause.message) }
            }
        }
    }

    // Ohne aktives Unternehmen laufen Dashboard/Rechnungsliste/etc. ins Leere -
    // bei Erstanlage direkt als aktiv setzen.
    private suspend fun ensureActiveCompany(company: Company) {
        val settings = appSettingsRepository.get()
        if (settings == null || settings.activeCompanyId != company.id) {
            val base = settings ?: AppSettings(
                activeCompanyId = null,
                invoiceNumberPrefix = "RE",
                defaultPaymentTermDays = company.defaultPaymentTermDays,
                defaultInvoiceNote = company.defaultInvoiceNote,
                smallBusinessNoticeText = "Gemäß § 19 UStG wird keine Umsatzsteuer berechnet.",
                pdfLayoutReference = null
            )
            appSettingsRepository.save(base.copy(activeCompanyId = company.id))
        }
    }

    private fun emptyForm() = CompanyProfileFormUiModel(
        id = null, name = "", street = "", houseNumber = "", postalCode = "", city = "",
        country = "Deutschland", email = "", phone = "", website = "", taxNumber = "", vatId = "",
        bankName = "", iban = "", bic = "", isSmallBusiness = false, defaultCurrency = "EUR",
        defaultPaymentTermDays = "14", defaultInvoiceNote = "", logo = null
    )

    private fun Company.toFormUiModel() = CompanyProfileFormUiModel(
        id = id, name = name, street = street, houseNumber = houseNumber, postalCode = postalCode,
        city = city, country = country, email = email, phone = phone, website = website,
        taxNumber = taxNumber, vatId = vatId, bankName = bankName, iban = iban, bic = bic,
        isSmallBusiness = isSmallBusiness, defaultCurrency = defaultCurrency,
        defaultPaymentTermDays = defaultPaymentTermDays.toString(), defaultInvoiceNote = defaultInvoiceNote, logo = logo
    )
}