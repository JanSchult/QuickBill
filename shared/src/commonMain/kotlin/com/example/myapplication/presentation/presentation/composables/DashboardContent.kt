package com.example.myapplication.presentation.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.presentation.viewmodel.uistate.DashboardUiState
import com.example.myapplication.util.formatEuro


@Composable
 fun DashboardContent(
    uiState: DashboardUiState,
    padding: PaddingValues,
    onNewInvoiceClick: () -> Unit,
    onNewCustomerClick: () -> Unit,
    onCompanyProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
    ) {
        Text("Umsatz diesen Monat", style = MaterialTheme.typography.labelLarge)
        Text(
            text = uiState.revenueThisMonth.formatEuro(),
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(count = 2),
            modifier = Modifier.height(200.dp)
        ) {
            // 1. Definiere die Daten ohne direkte Farben
            val stats = listOf(
                Triple("Entwürfe", uiState.draftCount, 0),
                Triple("Offen", uiState.openCount, 1),
                Triple("Überfällig", uiState.overdueCount, 2),
                Triple("Bezahlt", uiState.paidCount, 3)
            )

            items(stats) { (label, count, colorType) ->
                // 2. Greife auf MaterialTheme.colorScheme HIER im Composable-Kontext zu:
                val backgroundColor = when (colorType) {
                    0 -> MaterialTheme.colorScheme.surfaceVariant
                    1 -> MaterialTheme.colorScheme.secondaryContainer
                    2 -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.tertiaryContainer
                }

                StatCard(
                    label = label,
                    count = count,
                    containerColor = backgroundColor
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Schnellaktionen", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            QuickActionButton(Icons.Filled.Add, "Neue Rechnung", onNewInvoiceClick, Modifier.weight(1f))
            QuickActionButton(Icons.Filled.Group, "Neuer Kunde", onNewCustomerClick, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        QuickActionButton(Icons.Filled.Business, "Unternehmensprofil", onCompanyProfileClick, Modifier.fillMaxWidth())
    }
}