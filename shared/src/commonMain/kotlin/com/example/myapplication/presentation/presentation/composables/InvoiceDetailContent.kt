package com.example.myapplication.presentation.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.InvoiceStatus
import com.example.myapplication.presentation.presentation.viewmodel.event.InvoiceDetailEvent
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.InvoiceDetailUiModel

@Composable
 fun InvoiceDetailContent(
    invoice: InvoiceDetailUiModel,
    isProcessing: Boolean,
    padding: androidx.compose.foundation.layout.PaddingValues,
    onEvent: (InvoiceDetailEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(invoice.customerDisplayName, style = MaterialTheme.typography.titleMedium)
                    Text("Rechnungsdatum: ${invoice.invoiceDateDisplay}", style = MaterialTheme.typography.bodySmall)
                    Text("Fällig: ${invoice.dueDateDisplay}", style = MaterialTheme.typography.bodySmall)
                }
                StatusBadge(status = invoice.status)
            }
        }

        item { Text("Positionen", style = MaterialTheme.typography.titleMedium) }

        items(invoice.items) { item -> InvoiceDetailItemRow(item) }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SummaryLine("Netto", invoice.netAmountFormatted)
                    SummaryLine("Steuer", invoice.taxAmountFormatted)
                    HorizontalDivider()
                    SummaryLine("Brutto", invoice.grossAmountFormatted, emphasize = true)
                }
            }
        }

        if (invoice.note.isNotBlank() || invoice.paymentTerms.isNotBlank()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (invoice.paymentTerms.isNotBlank()) Text("Zahlungsbedingungen: ${invoice.paymentTerms}", style = MaterialTheme.typography.bodyMedium)
                    if (invoice.note.isNotBlank()) Text("Notiz: ${invoice.note}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { onEvent(InvoiceDetailEvent.GeneratePdf) },
                    enabled = false, // PDF folgt in Phase 6
                    modifier = Modifier.fillMaxWidth()
                ) { Text("PDF anzeigen / teilen (folgt)") }

                when (invoice.status) {
                    InvoiceStatus.OPEN -> Button(
                        onClick = { onEvent(InvoiceDetailEvent.MarkAsSent) },
                        enabled = !isProcessing,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Als versendet markieren") }

                    InvoiceStatus.SENT, InvoiceStatus.PARTIALLY_PAID, InvoiceStatus.OVERDUE -> Button(
                        onClick = { onEvent(InvoiceDetailEvent.MarkAsPaid) },
                        enabled = !isProcessing,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Als bezahlt markieren") }

                    else -> Unit
                }
            }
        }
    }
}