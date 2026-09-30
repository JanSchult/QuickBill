package com.example.myapplication.presentation.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.myapplication.presentation.presentation.composables.DashboardContent
import com.example.myapplication.presentation.presentation.composables.ErrorState
import com.example.myapplication.presentation.presentation.composables.LoadingState
import com.example.myapplication.presentation.presentation.viewmodel.DashboardViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNewInvoiceClick: () -> Unit,
    onNewCustomerClick: () -> Unit,
    onCompanyProfileClick: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Übersicht") }) }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingState(modifier = Modifier.padding(padding))
            uiState.errorMessage != null -> ErrorState(
                message = uiState.errorMessage ?: "",
                onDismiss = { },
                modifier = Modifier.padding(padding)
            )
            else -> DashboardContent(
                uiState = uiState,
                padding = padding,
                onNewInvoiceClick = onNewInvoiceClick,
                onNewCustomerClick = onNewCustomerClick,
                onCompanyProfileClick = onCompanyProfileClick
            )
        }
    }
}

