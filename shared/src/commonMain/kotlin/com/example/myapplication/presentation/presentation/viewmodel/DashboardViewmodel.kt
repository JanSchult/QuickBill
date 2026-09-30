package com.example.myapplication.presentation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Invoice
import com.example.myapplication.domain.model.InvoiceStatus
import com.example.myapplication.domain.model.Money
import com.example.myapplication.domain.repository.AppSettingsRepository
import com.example.myapplication.domain.repository.InvoiceRepository
import com.example.myapplication.presentation.presentation.viewmodel.uistate.DashboardUiState
import com.example.myapplication.util.AppClock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach


@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(
    private val appSettingsRepository: AppSettingsRepository,
    private val invoiceRepository: InvoiceRepository,
    private val clock: AppClock
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        appSettingsRepository.observe()
            .flatMapLatest { settings ->
                val companyId = settings?.activeCompanyId
                if (companyId == null) {
                    flowOf(DashboardUiState(isLoading = false, errorMessage = "Kein aktives Unternehmen ausgewählt."))
                } else {
                    invoiceRepository.observeAll(companyId).map { buildUiState(it) }
                }
            }
            .onEach { state -> _uiState.value = state }
            .catch { e -> _uiState.value = DashboardUiState(
                isLoading = false,
                errorMessage = e.message ?: "Unbekannter Fehler"
            )
            }
            .launchIn(viewModelScope)
    }

    private fun buildUiState(invoices: List<Invoice>): DashboardUiState {
        val today = clock.today()
        val revenue = invoices
            .filter { it.status == InvoiceStatus.PAID && it.paidAt != null }
            .filter { it.paidAt!!.date.month == today.month && it.paidAt.date.year == today.year }
            .fold(Money.ZERO) { acc, invoice -> acc + invoice.grossAmount }

        return DashboardUiState(
            isLoading = false,
            draftCount = invoices.count { it.status == InvoiceStatus.DRAFT },
            openCount = invoices.count { it.status == InvoiceStatus.OPEN || it.status == InvoiceStatus.SENT },
            overdueCount = invoices.count { it.status == InvoiceStatus.OVERDUE },
            paidCount = invoices.count { it.status == InvoiceStatus.PAID },
            revenueThisMonth = revenue
        )
    }
}