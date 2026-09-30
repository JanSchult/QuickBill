package com.example.myapplication.presentation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.InvoiceItem
import com.example.myapplication.domain.model.InvoiceWithItems
import com.example.myapplication.domain.repository.CustomerRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.domain.usecase.CancelInvoiceUseCase
import com.example.myapplication.domain.usecase.DuplicateInvoiceUseCase
import com.example.myapplication.domain.usecase.GetInvoiceDetailsUseCase
import com.example.myapplication.domain.usecase.MarkInvoiceAsPaidUseCase
import com.example.myapplication.domain.usecase.MarkInvoiceAsSentUseCase
import com.example.myapplication.presentation.presentation.viewmodel.event.InvoiceDetailEvent
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.InvoiceDetailItemUiModel
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.InvoiceDetailUiModel
import com.example.myapplication.presentation.presentation.viewmodel.uistate.InvoiceDetailUiState
import com.example.myapplication.util.displayName
import com.example.myapplication.util.formatEuro
import com.example.myapplication.util.formatGerman
import com.example.myapplication.util.thousandthsToDisplayString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InvoiceDetailViewModel(
    private val invoiceId: String,
    private val getInvoiceDetails: GetInvoiceDetailsUseCase,
    private val customerRepository: CustomerRepository,
    private val markInvoiceAsSent: MarkInvoiceAsSentUseCase,
    private val markInvoiceAsPaid: MarkInvoiceAsPaidUseCase,
    private val cancelInvoice: CancelInvoiceUseCase,
    private val duplicateInvoice: DuplicateInvoiceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InvoiceDetailUiState())
    val uiState: StateFlow<InvoiceDetailUiState> = _uiState.asStateFlow()

    init {
        getInvoiceDetails(invoiceId)
            .onEach { invoiceWithItems -> applyInvoiceWithItems(invoiceWithItems) }
            .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Unbekannter Fehler") } }
            .launchIn(viewModelScope)
    }

    private suspend fun applyInvoiceWithItems(invoiceWithItems: InvoiceWithItems?) {
        if (invoiceWithItems == null) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Rechnung nicht gefunden.") }
            return
        }

        // Falls customerId vorhanden ist, laden wir den Kunden, ansonsten null
        val customer = invoiceWithItems.invoice.customerId?.let { id ->
            customerRepository.getById(id)
        }

        _uiState.update {
            it.copy(
                isLoading = false,
                invoice = invoiceWithItems.toUiModel(customer?.displayName() ?: "Kein Kunde zugewiesen")
            )
        }
    }

    fun onEvent(event: InvoiceDetailEvent) {
        when (event) {
            is InvoiceDetailEvent.MarkAsSent -> runAction { markInvoiceAsSent(invoiceId) }
            is InvoiceDetailEvent.MarkAsPaid -> runAction { markInvoiceAsPaid(invoiceId) }
            is InvoiceDetailEvent.CancelInvoice -> runAction { cancelInvoice(invoiceId) }
            is InvoiceDetailEvent.Duplicate -> onDuplicate()
            is InvoiceDetailEvent.GeneratePdf -> Unit // TODO Phase 6: GenerateInvoicePdfUseCase
            is InvoiceDetailEvent.DismissError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun runAction(action: suspend () -> AppResult<*>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingAction = true) }
            when (val result = action()) {
                is AppResult.Failure -> _uiState.update { it.copy(isProcessingAction = false, errorMessage = result.cause.message) }
                is AppResult.ValidationError -> _uiState.update {
                    it.copy(isProcessingAction = false, errorMessage = result.errors.firstOrNull()?.message)
                }
                // Der beobachtende Flow oben aktualisiert invoice.status automatisch - kein manuelles Update nötig.
                is AppResult.Success -> _uiState.update { it.copy(isProcessingAction = false) }
            }
        }
    }

    private fun onDuplicate() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingAction = true) }
            when (val result = duplicateInvoice(invoiceId)) {
                is AppResult.Success -> _uiState.update { it.copy(isProcessingAction = false, duplicatedInvoiceId = result.data.id) }
                is AppResult.Failure -> _uiState.update { it.copy(isProcessingAction = false, errorMessage = result.cause.message) }
                is AppResult.ValidationError -> _uiState.update {
                    it.copy(isProcessingAction = false, errorMessage = result.errors.firstOrNull()?.message)
                }
            }
        }
    }

    private fun InvoiceWithItems.toUiModel(customerName: String): InvoiceDetailUiModel = InvoiceDetailUiModel(
        id = invoice.id,
        invoiceNumber = invoice.invoiceNumber,
        status = invoice.status,
        customerDisplayName = customerName,
        invoiceDateDisplay = invoice.invoiceDate.formatGerman(),
        dueDateDisplay = invoice.dueDate.formatGerman(),
        items = items.sortedBy { it.position }.map { it.toUiModel() },
        netAmountFormatted = invoice.netAmount.formatEuro(),
        taxAmountFormatted = invoice.taxAmount.formatEuro(),
        grossAmountFormatted = invoice.grossAmount.formatEuro(),
        note = invoice.note,
        paymentTerms = invoice.paymentTerms,
        pdfFileReference = invoice.pdfFileReference
    )

    private fun InvoiceItem.toUiModel() = InvoiceDetailItemUiModel(
        description = description,
        quantityAndUnitDisplay = "${quantityThousandths.thousandthsToDisplayString()} ${unit.displayLabel}",
        unitPriceDisplay = unitPriceNet.formatEuro(),
        taxRateDisplay = taxRate.asPercentageString(),
        netAmountFormatted = netAmount.formatEuro(),
        taxAmountFormatted = taxAmount.formatEuro(),
        grossAmountFormatted = grossAmount.formatEuro()
    )
}