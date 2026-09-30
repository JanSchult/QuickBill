package com.example.myapplication.presentation.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.myapplication.domain.model.InvoiceStatus
import com.example.myapplication.presentation.presentation.composables.ErrorState
import com.example.myapplication.presentation.presentation.composables.InvoiceDetailContent
import com.example.myapplication.presentation.presentation.composables.LoadingState
import com.example.myapplication.presentation.presentation.viewmodel.InvoiceDetailViewModel
import com.example.myapplication.presentation.presentation.viewmodel.event.InvoiceDetailEvent
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDetailScreen(
    invoiceId: String,
    onBack: () -> Unit,
    onDuplicated: (String) -> Unit,
    viewModel: InvoiceDetailViewModel = koinViewModel { parametersOf(invoiceId) }
) {
    val uiState by viewModel.uiState.collectAsState()
    var menuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.duplicatedInvoiceId) {
        uiState.duplicatedInvoiceId?.let(onDuplicated)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.invoice?.invoiceNumber ?: "Rechnungsdetails") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                },
                actions = {
                    if (uiState.invoice != null) {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Weitere Aktionen")
                        }
                        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            DropdownMenuItem(
                                text = { Text("Duplizieren") },
                                onClick = { menuExpanded = false; viewModel.onEvent(InvoiceDetailEvent.Duplicate) }
                            )
                            if (uiState.invoice?.status !in setOf(InvoiceStatus.PAID, InvoiceStatus.CANCELLED)) {
                                DropdownMenuItem(
                                    text = { Text("Stornieren") },
                                    onClick = { menuExpanded = false; viewModel.onEvent(InvoiceDetailEvent.CancelInvoice) }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingState(modifier = Modifier.padding(padding))
            uiState.errorMessage != null -> ErrorState(
                message = uiState.errorMessage ?: "",
                onDismiss = { viewModel.onEvent(InvoiceDetailEvent.DismissError) },
                modifier = Modifier.padding(padding)
            )
            uiState.invoice != null -> InvoiceDetailContent(
                invoice = uiState.invoice!!,
                isProcessing = uiState.isProcessingAction,
                padding = padding,
                onEvent = viewModel::onEvent
            )
        }
    }
}
