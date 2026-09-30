package com.example.myapplication.presentation.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.InvoiceDetailItemUiModel

@Composable
 fun InvoiceDetailItemRow(item: InvoiceDetailItemUiModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(item.description, style = MaterialTheme.typography.bodyLarge)
            Text(
                "${item.quantityAndUnitDisplay} × ${item.unitPriceDisplay} · ${item.taxRateDisplay}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Netto: ${item.netAmountFormatted}", style = MaterialTheme.typography.bodySmall)
                Text("Brutto: ${item.grossAmountFormatted}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}