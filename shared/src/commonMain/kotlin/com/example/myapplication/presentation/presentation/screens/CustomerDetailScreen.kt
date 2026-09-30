package com.example.myapplication.presentation.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.presentation.composables.CustomerDetailContent
import com.example.myapplication.presentation.presentation.composables.LoadingState
import com.example.myapplication.presentation.presentation.viewmodel.CustomerDetailViewModel
import com.example.myapplication.presentation.presentation.viewmodel.event.CustomerDetailEvent
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customerId: String?,
    companyId: String,
    onBack: () -> Unit,
    onInvoiceClick: (String) -> Unit,
    onDuplicated: (String) -> Unit,
    viewModel: CustomerDetailViewModel = koinViewModel { parametersOf(customerId, companyId) }
) {
    val uiState by viewModel.uiState.collectAsState()
    var menuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.duplicatedCustomerId) {
        uiState.duplicatedCustomerId?.let(onDuplicated)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isNewCustomer) "Neuer Kunde" else "Kunde bearbeiten") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                },
                actions = {
                    if (!uiState.isNewCustomer) {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Weitere Aktionen")
                        }
                        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            DropdownMenuItem(
                                text = { Text("Duplizieren") },
                                onClick = { menuExpanded = false; viewModel.onEvent(
                                    CustomerDetailEvent.Duplicate) }
                            )
                            if (uiState.form?.isArchived == true) {
                                DropdownMenuItem(
                                    text = { Text("Wiederherstellen") },
                                    onClick = { menuExpanded = false; viewModel.onEvent(CustomerDetailEvent.Restore) }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text("Archivieren") },
                                    onClick = { menuExpanded = false; viewModel.onEvent(CustomerDetailEvent.Archive) }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        val form = uiState.form

        if (uiState.isLoading || form == null) {
            LoadingState(modifier = Modifier.padding(padding))
            return@Scaffold
        }

        CustomerDetailContent(
            uiState = uiState,
            form = form,
            padding = padding,
            onEvent = viewModel::onEvent,
            onInvoiceClick = onInvoiceClick
        )
    }
}