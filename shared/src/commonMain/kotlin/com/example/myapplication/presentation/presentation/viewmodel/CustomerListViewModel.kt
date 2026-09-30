package com.example.myapplication.presentation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Customer
import com.example.myapplication.domain.model.CustomerSortOrder
import com.example.myapplication.domain.repository.CustomerRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.domain.usecase.DeleteCustomerUseCase
import com.example.myapplication.presentation.presentation.viewmodel.event.CustomerListEvent
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.CustomerListItemUiModel
import com.example.myapplication.presentation.presentation.viewmodel.uistate.CustomerListUiState
import com.example.myapplication.util.AppClock
import com.example.myapplication.util.displayName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CustomerListViewModel(
    private val companyId: String,
    private val clock: AppClock,
    private val customerRepository: CustomerRepository,
    private val deleteCustomer: DeleteCustomerUseCase
) : ViewModel() {

    // Eigener, schlanker Query-State statt Filterung direkt über uiState -
    // sonst würde jedes uiState-Update (auch das aus dem eigenen onEach) den
    // flatMapLatest erneut auslösen und eine unnötige Rückkopplungsschleife erzeugen.
    private data class Query(
        val searchQuery: String = "",
        val sortOrder: CustomerSortOrder = CustomerSortOrder.NAME,
        val showArchived: Boolean = false
    )

    private val query = MutableStateFlow(Query())
    private val _uiState = MutableStateFlow(CustomerListUiState())
    val uiState: StateFlow<CustomerListUiState> = _uiState.asStateFlow()

    init {
        query
            .flatMapLatest { q ->
                when {
                    q.searchQuery.isNotBlank() -> customerRepository.search(companyId, q.searchQuery)
                    q.showArchived -> customerRepository.observeArchived(companyId)
                    q.sortOrder == CustomerSortOrder.LAST_USED -> customerRepository.observeActiveSortedByLastUsed(companyId)
                    else -> customerRepository.observeActiveSortedByName(companyId)
                }
            }
            .onEach { customers ->
                val q = query.value
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        customers = customers.map { c -> c.toUiModel() },
                        searchQuery = q.searchQuery,
                        sortOrder = q.sortOrder,
                        showArchived = q.showArchived
                    )
                }
            }
            .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Unbekannter Fehler") } }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: CustomerListEvent) {
        when (event) {
            is CustomerListEvent.SearchQueryChanged -> query.update { it.copy(searchQuery = event.query) }
            is CustomerListEvent.SortOrderChanged -> query.update { it.copy(sortOrder = event.order) }
            is CustomerListEvent.ShowArchivedToggled -> query.update { it.copy(showArchived = event.showArchived) }
            is CustomerListEvent.ArchiveRequested -> viewModelScope.launch {
                customerRepository.archive(event.customerId, clock.nowDateTime())
            }
            is CustomerListEvent.RestoreRequested -> viewModelScope.launch {
                customerRepository.restore(event.customerId, clock.nowDateTime())
            }
            is CustomerListEvent.DeleteRequested -> onDelete(event.customerId)
            is CustomerListEvent.DismissError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun onDelete(customerId: String) {
        viewModelScope.launch {
            when (val result = deleteCustomer(customerId)) {
                is AppResult.Failure -> _uiState.update { it.copy(errorMessage = result.cause.message) }
                else -> Unit // Success/ValidationError (Archivierungs-Fallback) spiegeln sich über den Flow automatisch
            }
        }
    }

    private fun Customer.toUiModel() = CustomerListItemUiModel(
        id = id, displayName = displayName(), email = email, isArchived = isArchived
    )
}