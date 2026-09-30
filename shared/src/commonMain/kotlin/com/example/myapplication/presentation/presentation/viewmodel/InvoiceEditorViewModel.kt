package com.example.myapplication.presentation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Company
import com.example.myapplication.domain.model.Customer
import com.example.myapplication.domain.model.Invoice
import com.example.myapplication.domain.model.InvoiceItem
import com.example.myapplication.domain.model.InvoiceWithItems
import com.example.myapplication.domain.model.ItemUnit
import com.example.myapplication.domain.model.Money
import com.example.myapplication.domain.model.TaxRate
import com.example.myapplication.domain.repository.AppSettingsRepository
import com.example.myapplication.domain.repository.CompanyRepository
import com.example.myapplication.domain.repository.CustomerRepository
import com.example.myapplication.domain.repository.InvoiceItemRepository
import com.example.myapplication.domain.repository.InvoiceRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.domain.usecase.AddInvoiceItemUseCase
import com.example.myapplication.domain.usecase.CreateInvoiceDraftUseCase
import com.example.myapplication.domain.usecase.FinalizeInvoiceUseCase
import com.example.myapplication.domain.usecase.RemoveInvoiceItemUseCase
import com.example.myapplication.domain.usecase.UpdateInvoiceDraftUseCase
import com.example.myapplication.domain.usecase.UpdateInvoiceItemUseCase
import com.example.myapplication.presentation.presentation.viewmodel.event.InvoiceEditorEvent
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.CustomerUiModel
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.InvoiceDraftUiModel
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.InvoiceItemUiModel
import com.example.myapplication.presentation.presentation.viewmodel.uistate.InvoiceEditorUiState
import com.example.myapplication.util.AppClock
import com.example.myapplication.util.basisPointsToPercentDisplay
import com.example.myapplication.util.displayName
import com.example.myapplication.util.formatEuro
import com.example.myapplication.util.formatGerman
import com.example.myapplication.util.parsePercentToBasisPoints
import com.example.myapplication.util.parseToThousandths
import com.example.myapplication.util.thousandthsToDisplayString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * invoiceId ist nullable: null bedeutet "neue Rechnung" - der ViewModel legt beim Start
 * selbst einen Entwurf an (CreateInvoiceDraftUseCase) und arbeitet danach einheitlich
 * mit einer ID weiter, wie bei bestehenden Rechnungen auch.
 */
class InvoiceEditorViewModel(
    private val initialInvoiceId: String?,
    private val companyId: String,
    private val clock: AppClock,
    private val invoiceRepository: InvoiceRepository,
    private val invoiceItemRepository: InvoiceItemRepository,
    private val customerRepository: CustomerRepository,
    private val companyRepository: CompanyRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val createInvoiceDraft: CreateInvoiceDraftUseCase,
    private val updateInvoiceDraft: UpdateInvoiceDraftUseCase,
    private val addInvoiceItem: AddInvoiceItemUseCase,
    private val updateInvoiceItem: UpdateInvoiceItemUseCase,
    private val removeInvoiceItem: RemoveInvoiceItemUseCase,
    private val finalizeInvoice: FinalizeInvoiceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InvoiceEditorUiState())
    val uiState: StateFlow<InvoiceEditorUiState> = _uiState.asStateFlow()

    private var invoiceId: String? = initialInvoiceId
    private var lastKnownCompany: Company? = null
    // Zuletzt bekannter vollständiger Domain-Stand - Events tragen nur das geänderte
    // Feld, dieser Wert liefert den Rest für den updateInvoiceDraft-Aufruf.
    private var lastKnownInvoice: Invoice? = null

    init {
        viewModelScope.launch {
            val id = invoiceId ?: createDraftAndGetId()
            invoiceId = id
            observeEditorData(id)
        }
    }

    private suspend fun createDraftAndGetId(): String {
        val settings = appSettingsRepository.get()
        return when (
            val result = createInvoiceDraft(
                companyId = companyId,
                customerId = null,
                invoiceDate = clock.today(),
                paymentTermDays = settings?.defaultPaymentTermDays ?: 14,
                currency = "EUR",
                paymentTerms = settings?.defaultInvoiceNote ?: ""
            )
        ) {
            is AppResult.Success -> result.data.id
            is AppResult.ValidationError -> error("Entwurf konnte nicht erstellt werden: ${result.errors}")
            is AppResult.Failure -> throw result.cause
        }
    }

    private fun observeEditorData(id: String) {
        combine(
            invoiceRepository.observeWithItem(id),
            customerRepository.observeActiveSortedByName(companyId),
            companyRepository.observeCompany(companyId),
            appSettingsRepository.observe()
        ) { invoiceWithItems, customers, company, _ ->
            lastKnownCompany = company
            lastKnownInvoice = invoiceWithItems?.invoice
            buildUiState(invoiceWithItems, customers, company)
        }
            .onEach { state -> _uiState.value = state }
            .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Unbekannter Fehler") } }
            .launchIn(viewModelScope)
    }

    private fun buildUiState(
        invoiceWithItems: InvoiceWithItems?,
        customers: List<Customer>,
        company: Company?
    ): InvoiceEditorUiState {
        val invoice = invoiceWithItems?.invoice
            ?: return InvoiceEditorUiState(isLoading = true, customers = customers.map { it.toUiModel() })

        val draftUiModel = InvoiceDraftUiModel(
            id = invoice.id,
            status = invoice.status,
            invoiceNumber = invoice.invoiceNumber,
            customerId = invoice.customerId,
            invoiceDate = invoice.invoiceDate,                    // ## ergänzt
            invoiceDateDisplay = invoice.invoiceDate.formatGerman(),
            serviceDate = invoice.serviceDate,                    // ## ergänzt
            serviceDateDisplay = invoice.serviceDate?.formatGerman() ?: "",
            dueDate = invoice.dueDate,                            // ## ergänzt
            dueDateDisplay = invoice.dueDate.formatGerman(),
            paymentTerms = invoice.paymentTerms,
            note = invoice.note,
            additionalHint = invoice.additionalHint,
            items = invoiceWithItems.items.sortedBy { it.position }.map { it.toUiModel() },
            netAmountFormatted = invoice.netAmount.formatEuro(),
            taxAmountFormatted = invoice.taxAmount.formatEuro(),
            grossAmountFormatted = invoice.grossAmount.formatEuro(),
            isSmallBusinessNoticeShown = company?.isSmallBusiness == true
        )
        return InvoiceEditorUiState(
            isLoading = false,
            invoice = draftUiModel,
            customers = customers.map { it.toUiModel() }
        )
    }

    private fun Customer.toUiModel() = CustomerUiModel(id = id, displayName = displayName())

    private fun InvoiceItem.toUiModel() = InvoiceItemUiModel(
        id = id,
        position = position,
        description = description,
        quantityDisplay = quantityThousandths.thousandthsToDisplayString(),
        unit = unit,
        unitPriceDisplay = unitPriceNet.formatEuro(),
        discountDisplay = discountBasisPoints.basisPointsToPercentDisplay(),
        taxRateDisplay = taxRate.basisPoints.basisPointsToPercentDisplay(),
        netAmountFormatted = netAmount.formatEuro(),
        taxAmountFormatted = taxAmount.formatEuro(),
        grossAmountFormatted = grossAmount.formatEuro()
    )

    fun onEvent(event: InvoiceEditorEvent) {
        val id = invoiceId ?: return
        when (event) {
            is InvoiceEditorEvent.CustomerSelected -> updateHeader(id) { it.copy(customerId = event.customerId) }
            is InvoiceEditorEvent.InvoiceDateChanged -> updateHeader(id) { it.copy(invoiceDate = event.date) }
            is InvoiceEditorEvent.DueDateChanged -> updateHeader(id) { it.copy(dueDate = event.date) }
            is InvoiceEditorEvent.ServiceDateChanged -> updateHeader(id) { it.copy(serviceDate = event.date) }
            is InvoiceEditorEvent.PaymentTermsChanged -> updateHeader(id) { it.copy(paymentTerms = event.value) }
            is InvoiceEditorEvent.NoteChanged -> updateHeader(id) { it.copy(note = event.value) }
            is InvoiceEditorEvent.AdditionalHintChanged -> updateHeader(id) { it.copy(additionalHint = event.value) }

            is InvoiceEditorEvent.AddItem -> onAddItem(id)
            is InvoiceEditorEvent.DescriptionChanged -> onItemFieldChanged(id, event.itemId) { it.copy(description = event.value) }
            is InvoiceEditorEvent.QuantityChanged -> onItemFieldChanged(id, event.itemId) { it.copy(quantityThousandths = event.value.parseToThousandths()) }
            is InvoiceEditorEvent.UnitChanged -> onItemFieldChanged(id, event.itemId) { it.copy(unit = event.unit) }
            is InvoiceEditorEvent.UnitPriceChanged -> onItemFieldChanged(id, event.itemId) { it.copy(unitPriceNet = Money.fromDecimalString(event.value)) }
            is InvoiceEditorEvent.DiscountChanged -> onItemFieldChanged(id, event.itemId) { it.copy(discountBasisPoints = event.value.parsePercentToBasisPoints()) }
            is InvoiceEditorEvent.TaxRateChanged -> onItemFieldChanged(id, event.itemId) { it.copy(taxRate = TaxRate(event.value.parsePercentToBasisPoints())) }
            is InvoiceEditorEvent.RemoveItem -> onRemoveItem(id, event.itemId)

            is InvoiceEditorEvent.SaveDraft -> onSaveDraft()
            is InvoiceEditorEvent.FinalizeInvoice -> onFinalize(id)
            is InvoiceEditorEvent.DismissError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun updateHeader(id: String, transform: (Invoice) -> Invoice) {
        viewModelScope.launch {
            val current = lastKnownInvoice ?: invoiceRepository.getById(id) ?: return@launch
            val updated = transform(current)
            val result = updateInvoiceDraft(
                invoiceId = id,
                customerId = updated.customerId ?: "",
                invoiceDate = updated.invoiceDate,
                dueDate = updated.dueDate,
                serviceDate = updated.serviceDate,
                servicePeriodFrom = updated.servicePeriodFrom,
                servicePeriodTo = updated.servicePeriodTo,
                paymentTerms = updated.paymentTerms,
                note = updated.note,
                additionalHint = updated.additionalHint
            )
            handleResult(result)
        }
    }

    private fun onAddItem(id: String) {
        viewModelScope.launch {
            val isSmallBusiness = lastKnownCompany?.isSmallBusiness ?: false
            val defaultTaxRate = if (isSmallBusiness) TaxRate.ZERO else TaxRate.STANDARD_DE
            val result = addInvoiceItem(
                invoiceId = id,
                description = "",
                quantityThousandths = 1000L,
                unit = ItemUnit.PIECE,
                unitPriceNet = Money.ZERO,
                discountBasisPoints = 0,
                taxRate = defaultTaxRate,
                isSmallBusiness = isSmallBusiness
            )
            handleResult(result)
        }
    }

    private fun onItemFieldChanged(invoiceId: String, itemId: String, transform: (InvoiceItem) -> InvoiceItem) {
        viewModelScope.launch {
            val currentItem = invoiceItemRepository.getForInvoice(invoiceId).firstOrNull { it.id == itemId }
                ?: return@launch
            val isSmallBusiness = lastKnownCompany?.isSmallBusiness ?: false
            val result = updateInvoiceItem(transform(currentItem), isSmallBusiness)
            handleResult(result)
        }
    }

    private fun onRemoveItem(invoiceId: String, itemId: String) {
        viewModelScope.launch {
            val isSmallBusiness = lastKnownCompany?.isSmallBusiness ?: false
            handleResult(removeInvoiceItem(invoiceId, itemId, isSmallBusiness))
        }
    }

    private fun onSaveDraft() {
        // Persistenz läuft bereits laufend bei jeder Feldänderung (updateHeader/onItemFieldChanged) -
        // dieser Event ist in v1 nur die Nutzerrückmeldung "gespeichert", kein zusätzlicher Schreibvorgang.
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun onFinalize(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val settings = appSettingsRepository.get()
            val isSmallBusiness = lastKnownCompany?.isSmallBusiness ?: false
            when (
                val result = finalizeInvoice(
                    invoiceId = id,
                    invoiceNumberPrefix = settings?.invoiceNumberPrefix ?: "RE",
                    isSmallBusiness = isSmallBusiness
                )
            ) {
                is AppResult.Success -> _uiState.update { it.copy(isSaving = false, isFinalized = true, validationErrors = emptyMap()) }
                is AppResult.ValidationError -> _uiState.update {
                    it.copy(isSaving = false, validationErrors = result.errors.associate { e -> e.field to e.message })
                }
                is AppResult.Failure -> _uiState.update { it.copy(isSaving = false, errorMessage = result.cause.message) }
            }
        }
    }

    private fun handleResult(result: AppResult<*>) {
        when (result) {
            is AppResult.Failure -> _uiState.update { it.copy(errorMessage = result.cause.message) }
            is AppResult.ValidationError -> _uiState.update {
                it.copy(validationErrors = result.errors.associate { e -> e.field to e.message })
            }
            is AppResult.Success -> _uiState.update { it.copy(validationErrors = emptyMap()) }
        }
    }
}