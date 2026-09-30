package com.example.myapplication.presentation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Customer
import com.example.myapplication.domain.model.Invoice
import com.example.myapplication.domain.model.InvoiceStatus
import com.example.myapplication.domain.model.Money
import com.example.myapplication.domain.repository.CustomerRepository
import com.example.myapplication.domain.repository.InvoiceRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.domain.usecase.CreateCustomerUseCase
import com.example.myapplication.domain.usecase.DuplicateCustomerUseCase
import com.example.myapplication.domain.usecase.UpdateCustomerUseCase
import com.example.myapplication.presentation.presentation.viewmodel.event.CustomerDetailEvent
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.CustomerFormUiModel
import com.example.myapplication.presentation.presentation.viewmodel.uistate.CustomerDetailUiState
import com.example.myapplication.util.AppClock
import com.example.myapplication.util.formatEuro
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.CustomerHistoryInvoiceUiModel

/** customerId == null bedeutet "neuer Kunde". */
class CustomerDetailViewModel(
    private val customerId: String?,
    private val companyId: String,
    private val clock: AppClock,
    private val customerRepository: CustomerRepository,
    private val invoiceRepository: InvoiceRepository,
    private val createCustomer: CreateCustomerUseCase,
    private val updateCustomer: UpdateCustomerUseCase,
    private val duplicateCustomer: DuplicateCustomerUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CustomerDetailUiState(isLoading = customerId != null, isNewCustomer = customerId == null)
    )
    val uiState: StateFlow<CustomerDetailUiState> = _uiState.asStateFlow()

    private var sourceCustomer: Customer? = null

    init {
        if (customerId == null) {
            _uiState.update { it.copy(isLoading = false, form = emptyForm()) }
        } else {
            loadCustomer(customerId)
            observeHistory(customerId)
        }
    }

    private fun loadCustomer(id: String) {
        viewModelScope.launch {
            val customer = customerRepository.getById(id)
            if (customer == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Kunde nicht gefunden.") }
                return@launch
            }
            sourceCustomer = customer
            _uiState.update { it.copy(isLoading = false, form = customer.toFormUiModel()) }
        }
    }

    private fun observeHistory(id: String) {
        invoiceRepository.observeByCustomer(id)
            .onEach { invoices -> applyHistory(invoices) }
            .catch { e -> _uiState.update { it.copy(errorMessage = e.message ?: "Unbekannter Fehler") } }
            .launchIn(viewModelScope)
    }

    private fun applyHistory(invoices: List<Invoice>) {
        val openStatuses = setOf(InvoiceStatus.OPEN, InvoiceStatus.SENT, InvoiceStatus.PARTIALLY_PAID, InvoiceStatus.OVERDUE)
        val open = invoices.filter { it.status in openStatuses }
        val totalRevenue = invoices.filter { it.status == InvoiceStatus.PAID }
            .fold(Money.ZERO) { acc, inv -> acc + inv.grossAmount }

        _uiState.update {
            it.copy(
                openInvoices = open.map { inv ->
                    CustomerHistoryInvoiceUiModel(
                        id = inv.id,
                        invoiceNumber = inv.invoiceNumber,
                        statusDisplay = inv.status.name,
                        grossAmountFormatted = inv.grossAmount.formatEuro()
                    )
                } as List<CustomerHistoryInvoiceUiModel>,
                totalRevenueFormatted = totalRevenue.formatEuro()
            )
        }
    }

    fun onEvent(event: CustomerDetailEvent) {
        val form = _uiState.value.form ?: return
        when (event) {
            is CustomerDetailEvent.CustomerNumberChanged -> updateForm(form.copy(customerNumber = event.value))
            is CustomerDetailEvent.CompanyNameChanged -> updateForm(form.copy(companyName = event.value))
            is CustomerDetailEvent.FirstNameChanged -> updateForm(form.copy(firstName = event.value))
            is CustomerDetailEvent.LastNameChanged -> updateForm(form.copy(lastName = event.value))
            is CustomerDetailEvent.ContactPersonChanged -> updateForm(form.copy(contactPerson = event.value))
            is CustomerDetailEvent.StreetChanged -> updateForm(form.copy(street = event.value))
            is CustomerDetailEvent.HouseNumberChanged -> updateForm(form.copy(houseNumber = event.value))
            is CustomerDetailEvent.PostalCodeChanged -> updateForm(form.copy(postalCode = event.value))
            is CustomerDetailEvent.CityChanged -> updateForm(form.copy(city = event.value))
            is CustomerDetailEvent.CountryChanged -> updateForm(form.copy(country = event.value))
            is CustomerDetailEvent.EmailChanged -> updateForm(form.copy(email = event.value))
            is CustomerDetailEvent.PhoneChanged -> updateForm(form.copy(phone = event.value))
            is CustomerDetailEvent.VatIdChanged -> updateForm(form.copy(vatId = event.value))
            is CustomerDetailEvent.NotesChanged -> updateForm(form.copy(notes = event.value))
            is CustomerDetailEvent.IsPrivateCustomerChanged -> updateForm(form.copy(isPrivateCustomer = event.value))

            is CustomerDetailEvent.Save -> onSave(form)
            is CustomerDetailEvent.Duplicate -> onDuplicate()
            is CustomerDetailEvent.Archive -> onArchiveRestore(archive = true)
            is CustomerDetailEvent.Restore -> onArchiveRestore(archive = false)
            is CustomerDetailEvent.DismissError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun updateForm(newForm: CustomerFormUiModel) {
        _uiState.update { it.copy(form = newForm, isSaved = false) }
    }

    private fun onSave(form: CustomerFormUiModel) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val result = if (_uiState.value.isNewCustomer) {
                createCustomer(
                    companyId = companyId,
                    customerNumber = form.customerNumber,
                    companyName = form.companyName,
                    firstName = form.firstName,
                    lastName = form.lastName,
                    contactPerson = form.contactPerson,
                    street = form.street,
                    houseNumber = form.houseNumber,
                    postalCode = form.postalCode,
                    city = form.city,
                    country = form.country,
                    email = form.email,
                    phone = form.phone,
                    vatId = form.vatId,
                    defaultPaymentTermDays = null,
                    notes = form.notes,
                    isPrivateCustomer = form.isPrivateCustomer
                )
            } else {
                val existing = sourceCustomer ?: return@launch
                updateCustomer(
                    existing.copy(
                        customerNumber = form.customerNumber,
                        companyName = form.companyName,
                        firstName = form.firstName,
                        lastName = form.lastName,
                        contactPerson = form.contactPerson,
                        street = form.street,
                        houseNumber = form.houseNumber,
                        postalCode = form.postalCode,
                        city = form.city,
                        country = form.country,
                        email = form.email,
                        phone = form.phone,
                        vatId = form.vatId,
                        notes = form.notes,
                        isPrivateCustomer = form.isPrivateCustomer
                    )
                )
            }

            when (result) {
                is AppResult.Success -> {
                    sourceCustomer = result.data
                    _uiState.update {
                        it.copy(
                            isSaving = false, isSaved = true, validationErrors = emptyMap(),
                            isNewCustomer = false, form = result.data.toFormUiModel()
                        )
                    }
                }
                is AppResult.ValidationError -> _uiState.update {
                    it.copy(isSaving = false, validationErrors = result.errors.associate { e -> e.field to e.message })
                }
                is AppResult.Failure -> _uiState.update { it.copy(isSaving = false, errorMessage = result.cause.message) }
            }
        }
    }

    private fun onDuplicate() {
        val id = customerId ?: return
        viewModelScope.launch {
            when (val result = duplicateCustomer(id)) {
                is AppResult.Success -> _uiState.update { it.copy(duplicatedCustomerId = result.data.id) }
                is AppResult.Failure -> _uiState.update { it.copy(errorMessage = result.cause.message) }
                is AppResult.ValidationError -> _uiState.update {
                    it.copy(errorMessage = result.errors.firstOrNull()?.message)
                }
            }
        }
    }

    private fun onArchiveRestore(archive: Boolean) {
        val id = customerId ?: return
        viewModelScope.launch {
            if (archive) customerRepository.archive(id, clock.nowDateTime())
            else customerRepository.restore(id, clock.nowDateTime())
            loadCustomer(id)
        }
    }

    private fun emptyForm() = CustomerFormUiModel(
        id = "", customerNumber = "", companyName = "", firstName = "", lastName = "",
        contactPerson = "", street = "", houseNumber = "", postalCode = "", city = "",
        country = "Deutschland", email = "", phone = "", vatId = "", notes = "",
        isPrivateCustomer = true, isArchived = false
    )

    private fun Customer.toFormUiModel() = CustomerFormUiModel(
        id = id, customerNumber = customerNumber, companyName = companyName,
        firstName = firstName, lastName = lastName, contactPerson = contactPerson,
        street = street, houseNumber = houseNumber, postalCode = postalCode, city = city,
        country = country, email = email, phone = phone, vatId = vatId, notes = notes,
        isPrivateCustomer = isPrivateCustomer, isArchived = isArchived
    )
}