package com.example.myapplication.presentation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Invoice
import com.example.myapplication.domain.repository.AppSettingsRepository
import com.example.myapplication.domain.repository.CustomerRepository
import com.example.myapplication.domain.repository.InvoiceRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.domain.usecase.DeleteInvoiceDraftUseCase
import com.example.myapplication.presentation.presentation.viewmodel.event.InvoiceListEvent
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.InvoiceListItemUiModel
import com.example.myapplication.presentation.presentation.viewmodel.uistate.InvoiceListUiState
import com.example.myapplication.util.displayName
import com.example.myapplication.util.formatEuro
import com.example.myapplication.util.formatGerman
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InvoiceListViewModel(
    private val appSettingsRepository: AppSettingsRepository,
    private val invoiceRepository: InvoiceRepository,
    private val customerRepository: CustomerRepository,
    private val deleteInvoiceDraft: DeleteInvoiceDraftUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InvoiceListUiState())
    val uiState: StateFlow<InvoiceListUiState> = _uiState.asStateFlow()

    init {
        observeInvoices()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeInvoices() {
        appSettingsRepository.observe()
            .flatMapLatest { settings ->
                val companyId = settings?.activeCompanyId
                if (companyId == null) {
                    flowOf(emptyList<Invoice>() to emptyMap<String, String>())
                } else {
                    val invoicesFlow = if (_uiState.value.searchQuery.isBlank()) {
                        invoiceRepository.observeAll(companyId)
                    } else {
                        invoiceRepository.search(companyId, _uiState.value.searchQuery)
                    }
                    val customersFlow = customerRepository.observeActiveSortedByName(companyId)
                    invoicesFlow.combine(customersFlow) { invoices, customers ->
                        invoices to customers.associate { it.id to it.displayName() }
                    }
                }
            }
            .onEach { (invoices, nameById) ->
                val filtered = _uiState.value.statusFilter?.let { status ->
                    invoices.filter { it.status == status }
                } ?: invoices

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        invoices = filtered.map { invoice ->
                            invoice.toUiModel(invoice.customerId?.let { nameById[it] })   // ## geändert
                        }
                    )
                }
            }
            .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Unbekannter Fehler") } }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: InvoiceListEvent) {
        when (event) {
            is InvoiceListEvent.SearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
                observeInvoices() // Query hat sich geändert - Beobachtung mit neuem Filter neu aufsetzen
            }
            is InvoiceListEvent.StatusFilterChanged -> {
                _uiState.update { it.copy(statusFilter = event.status) }
                observeInvoices()
            }
            is InvoiceListEvent.DeleteDraftRequested -> onDeleteDraft(event.invoiceId)
            is InvoiceListEvent.DismissError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun onDeleteDraft(invoiceId: String) {
        viewModelScope.launch {
            when (val result = deleteInvoiceDraft(invoiceId)) {
                is AppResult.Failure -> _uiState.update { it.copy(errorMessage = result.cause.message) }
                is AppResult.ValidationError -> _uiState.update {
                    it.copy(errorMessage = result.errors.firstOrNull()?.message)
                }
                is AppResult.Success -> Unit // Flow aktualisiert die Liste automatisch
            }
        }
    }

    private fun Invoice.toUiModel(customerName: String?) = InvoiceListItemUiModel(
        id = id,
        invoiceNumber = invoiceNumber,
        customerDisplayName = customerName ?: "Kein Kunde ausgewählt",
        invoiceDateFormatted = invoiceDate.formatGerman(),
        grossAmountFormatted = grossAmount.formatEuro(),
        status = status
    )
}