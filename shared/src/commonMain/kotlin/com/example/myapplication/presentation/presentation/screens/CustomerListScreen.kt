package com.example.myapplication.presentation.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.CustomerSortOrder
import com.example.myapplication.presentation.presentation.composables.CustomerListRow
import com.example.myapplication.presentation.presentation.composables.EmptyState
import com.example.myapplication.presentation.presentation.composables.ErrorState
import com.example.myapplication.presentation.presentation.composables.LoadingState
import com.example.myapplication.presentation.presentation.viewmodel.CustomerListViewModel
import com.example.myapplication.presentation.presentation.viewmodel.event.CustomerListEvent
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(
    companyId: String,
    onCustomerClick: (String) -> Unit,
    onNewCustomerClick: () -> Unit,
    viewModel: CustomerListViewModel = koinViewModel { parametersOf(companyId) }
) {
    val uiState by viewModel.uiState.collectAsState()
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kunden") },
                actions = {
                    IconButton(onClick = { sortMenuExpanded = true }) {
                        Icon(Icons.Filled.Sort, contentDescription = "Sortierung")
                    }
                    DropdownMenu(expanded = sortMenuExpanded, onDismissRequest = { sortMenuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Nach Name") },
                            onClick = {
                                sortMenuExpanded = false
                                viewModel.onEvent(CustomerListEvent.SortOrderChanged(
                                    CustomerSortOrder.NAME))
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Zuletzt verwendet") },
                            onClick = {
                                sortMenuExpanded = false
                                viewModel.onEvent(CustomerListEvent.SortOrderChanged(CustomerSortOrder.LAST_USED))
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewCustomerClick) {
                Icon(Icons.Filled.Add, contentDescription = "Neuer Kunde")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onEvent(CustomerListEvent.SearchQueryChanged(it)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                placeholder = { Text("Kunde suchen") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                FilterChip(
                    selected = !uiState.showArchived,
                    onClick = { viewModel.onEvent(CustomerListEvent.ShowArchivedToggled(false)) },
                    label = { Text("Aktiv") }
                )
                FilterChip(
                    selected = uiState.showArchived,
                    onClick = { viewModel.onEvent(CustomerListEvent.ShowArchivedToggled(true)) },
                    label = { Text("Archiviert") }
                )
            }

            when {
                uiState.isLoading -> LoadingState()
                uiState.errorMessage != null -> ErrorState(
                    message = uiState.errorMessage ?: "",
                    onDismiss = { viewModel.onEvent(CustomerListEvent.DismissError) }
                )
                uiState.isEmpty -> EmptyState(message = "Noch keine Kunden vorhanden.")
                else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.customers, key = { it.id }) { customer ->
                        CustomerListRow(
                            customer = customer,
                            onClick = { onCustomerClick(customer.id) },
                            onArchive = { viewModel.onEvent(CustomerListEvent.ArchiveRequested(customer.id)) },
                            onRestore = { viewModel.onEvent(CustomerListEvent.RestoreRequested(customer.id)) },
                            onDelete = { viewModel.onEvent(CustomerListEvent.DeleteRequested(customer.id)) }
                        )
                    }
                }
            }
        }
    }
}
