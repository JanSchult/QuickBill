package com.example.myapplication.presentation.presentation.screens

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.presentation.composables.EmptyState
import com.example.myapplication.presentation.presentation.composables.ErrorState
import com.example.myapplication.presentation.presentation.composables.InvoiceListRow
import com.example.myapplication.presentation.presentation.composables.LoadingState
import com.example.myapplication.presentation.presentation.composables.StatusFilterRow
import com.example.myapplication.presentation.presentation.viewmodel.InvoiceListViewModel
import com.example.myapplication.presentation.presentation.viewmodel.event.InvoiceListEvent
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceListScreen(
    onInvoiceClick: (String) -> Unit,
    onNewInvoiceClick: () -> Unit,
    viewModel: InvoiceListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Rechnungen") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewInvoiceClick) {
                Icon(Icons.Filled.Add, contentDescription = "Neue Rechnung")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onEvent(InvoiceListEvent.SearchQueryChanged(it)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                placeholder = { Text("Rechnungsnummer oder Kunde suchen") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            StatusFilterRow(
                selected = uiState.statusFilter,
                onSelect = { viewModel.onEvent(InvoiceListEvent.StatusFilterChanged(it)) }
            )

            when {
                uiState.isLoading -> LoadingState()
                uiState.errorMessage != null -> ErrorState(
                    message = uiState.errorMessage ?: "",
                    onDismiss = { viewModel.onEvent(InvoiceListEvent.DismissError) }
                )
                uiState.isEmpty -> EmptyState(message = "Noch keine Rechnungen vorhanden.")
                else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.invoices, key = { it.id }) { invoice ->
                        InvoiceListRow(
                            invoice = invoice,
                            onClick = { onInvoiceClick(invoice.id) },
                            onDeleteDraft = { viewModel.onEvent(InvoiceListEvent.DeleteDraftRequested(invoice.id)) }
                        )
                    }
                }
            }
        }
    }
}



