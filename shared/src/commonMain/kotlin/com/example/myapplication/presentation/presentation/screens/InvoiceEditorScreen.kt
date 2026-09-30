package com.example.myapplication.presentation.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.presentation.composables.CustomerSection
import com.example.myapplication.presentation.presentation.composables.DatesSection
import com.example.myapplication.presentation.presentation.composables.InvoiceItemEditorRow
import com.example.myapplication.presentation.presentation.composables.LoadingState
import com.example.myapplication.presentation.presentation.composables.TotalsSection
import com.example.myapplication.presentation.presentation.viewmodel.InvoiceEditorViewModel
import com.example.myapplication.presentation.presentation.viewmodel.event.InvoiceEditorEvent
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceEditorScreen(
    invoiceId: String?,
    companyId: String,
    onBack: () -> Unit,
    onFinalized: (String) -> Unit,
    viewModel: InvoiceEditorViewModel = koinViewModel { parametersOf(invoiceId, companyId) }
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isFinalized) {
        if (uiState.isFinalized) {
            uiState.invoice?.id?.let(onFinalized)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.invoice?.invoiceNumber ?: "Neue Rechnung") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        }
    ) { padding ->
        val invoice = uiState.invoice

        if (uiState.isLoading || invoice == null) {
            LoadingState(modifier = Modifier.padding(padding))
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CustomerSection(
                    customers = uiState.customers,
                    selectedCustomerId = invoice.customerId,
                    error = uiState.validationErrors["customerId"],
                    onCustomerSelected = { viewModel.onEvent(InvoiceEditorEvent.CustomerSelected(it)) }
                )
            }

            item {
                DatesSection(
                    invoice = invoice,
                    errors = uiState.validationErrors,
                    onInvoiceDateChanged = { viewModel.onEvent(InvoiceEditorEvent.InvoiceDateChanged(it)) },
                    onServiceDateChanged = { viewModel.onEvent(InvoiceEditorEvent.ServiceDateChanged(it)) },
                    onDueDateChanged = { viewModel.onEvent(InvoiceEditorEvent.DueDateChanged(it)) }
                )
            }

            item { Text("Rechnungspositionen", style = MaterialTheme.typography.titleMedium) }

            items(invoice.items, key = { it.id }) { item ->
                InvoiceItemEditorRow(
                    item = item,
                    onDescriptionChanged = { viewModel.onEvent(InvoiceEditorEvent.DescriptionChanged(item.id, it)) },
                    onQuantityChanged = { viewModel.onEvent(InvoiceEditorEvent.QuantityChanged(item.id, it)) },
                    onUnitChanged = { viewModel.onEvent(InvoiceEditorEvent.UnitChanged(item.id, it)) },
                    onUnitPriceChanged = { viewModel.onEvent(InvoiceEditorEvent.UnitPriceChanged(item.id, it)) },
                    onDiscountChanged = { viewModel.onEvent(InvoiceEditorEvent.DiscountChanged(item.id, it)) },
                    onTaxRateChanged = { viewModel.onEvent(InvoiceEditorEvent.TaxRateChanged(item.id, it)) },
                    onRemove = { viewModel.onEvent(InvoiceEditorEvent.RemoveItem(item.id)) }
                )
            }

            item {
                Column {
                    OutlinedButton(
                        onClick = { viewModel.onEvent(InvoiceEditorEvent.AddItem) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("+ Position hinzufügen") }
                    uiState.validationErrors["items"]?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item { TotalsSection(invoice = invoice) }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Notizen & Zahlungsbedingungen", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = invoice.paymentTerms,
                        onValueChange = { viewModel.onEvent(InvoiceEditorEvent.PaymentTermsChanged(it)) },
                        label = { Text("Zahlungsbedingungen") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = invoice.note,
                        onValueChange = { viewModel.onEvent(InvoiceEditorEvent.NoteChanged(it)) },
                        label = { Text("Notiz") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = invoice.additionalHint,
                        onValueChange = { viewModel.onEvent(InvoiceEditorEvent.AdditionalHintChanged(it)) },
                        label = { Text("Zusätzlicher Hinweis") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { viewModel.onEvent(InvoiceEditorEvent.SaveDraft) },
                            modifier = Modifier.weight(1f)
                        ) { Text("Als Entwurf speichern") }

                        Button(
                            onClick = { viewModel.onEvent(InvoiceEditorEvent.FinalizeInvoice) },
                            enabled = !uiState.isSaving && invoice.items.isNotEmpty(),
                            modifier = Modifier.weight(1f)
                        ) { Text(if (uiState.isSaving) "Wird finalisiert…" else "Rechnung finalisieren") }
                    }
                    // PDF-Vorschau folgt in Phase 6 (GenerateInvoicePdfUseCase) - Button bewusst als Platzhalter sichtbar
                    OutlinedButton(onClick = { }, enabled = false, modifier = Modifier.fillMaxWidth()) {
                        Text("PDF-Vorschau (folgt)")
                    }
                }
            }
        }
    }
}