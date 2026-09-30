package com.example.myapplication.presentation.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.presentation.viewmodel.event.CustomerDetailEvent
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.CustomerFormUiModel
import com.example.myapplication.presentation.presentation.viewmodel.uistate.CustomerDetailUiState

@Composable
 fun CustomerDetailContent(
    uiState: CustomerDetailUiState,
    form: CustomerFormUiModel,
    padding: PaddingValues,
    onEvent: (CustomerDetailEvent) -> Unit,
    onInvoiceClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Stammdaten", style = MaterialTheme.typography.titleMedium) }

        item {
            OutlinedTextField(
                value = form.customerNumber,
                onValueChange = { onEvent(CustomerDetailEvent.CustomerNumberChanged(it)) },
                label = { Text("Kundennummer") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Checkbox(
                    checked = form.isPrivateCustomer,
                    onCheckedChange = { onEvent(CustomerDetailEvent.IsPrivateCustomerChanged(it)) }
                )
                Text("Privatkunde", modifier = Modifier.padding(top = 12.dp))
            }
        }

        item {
            OutlinedTextField(
                value = form.companyName,
                onValueChange = { onEvent(CustomerDetailEvent.CompanyNameChanged(it)) },
                label = { Text("Firmenname") },
                isError = uiState.validationErrors.containsKey("companyName"),
                supportingText = uiState.validationErrors["companyName"]?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = form.firstName,
                    onValueChange = { onEvent(CustomerDetailEvent.FirstNameChanged(it)) },
                    label = { Text("Vorname") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = form.lastName,
                    onValueChange = { onEvent(CustomerDetailEvent.LastNameChanged(it)) },
                    label = { Text("Nachname") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            OutlinedTextField(
                value = form.contactPerson,
                onValueChange = { onEvent(CustomerDetailEvent.ContactPersonChanged(it)) },
                label = { Text("Ansprechpartner") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item { Text("Adresse", style = MaterialTheme.typography.titleMedium) }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = form.street,
                    onValueChange = { onEvent(CustomerDetailEvent.StreetChanged(it)) },
                    label = { Text("Straße") },
                    modifier = Modifier.weight(2f)
                )
                OutlinedTextField(
                    value = form.houseNumber,
                    onValueChange = { onEvent(CustomerDetailEvent.HouseNumberChanged(it)) },
                    label = { Text("Nr.") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = form.postalCode,
                    onValueChange = { onEvent(CustomerDetailEvent.PostalCodeChanged(it)) },
                    label = { Text("PLZ") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = form.city,
                    onValueChange = { onEvent(CustomerDetailEvent.CityChanged(it)) },
                    label = { Text("Ort") },
                    modifier = Modifier.weight(2f)
                )
            }
        }

        item {
            OutlinedTextField(
                value = form.country,
                onValueChange = { onEvent(CustomerDetailEvent.CountryChanged(it)) },
                label = { Text("Land") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item { Text("Kontakt & Steuer", style = MaterialTheme.typography.titleMedium) }

        item {
            OutlinedTextField(
                value = form.email,
                onValueChange = { onEvent(CustomerDetailEvent.EmailChanged(it)) },
                label = { Text("E-Mail") },
                isError = uiState.validationErrors.containsKey("email"),
                supportingText = uiState.validationErrors["email"]?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = form.phone,
                onValueChange = { onEvent(CustomerDetailEvent.PhoneChanged(it)) },
                label = { Text("Telefon") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = form.vatId,
                onValueChange = { onEvent(CustomerDetailEvent.VatIdChanged(it)) },
                label = { Text("Umsatzsteuer-ID") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = form.notes,
                onValueChange = { onEvent(CustomerDetailEvent.NotesChanged(it)) },
                label = { Text("Notizen") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (uiState.openInvoices.isNotEmpty() || uiState.totalRevenueFormatted != "0,00 €") {
            item { HorizontalDivider() }
            item { Text("Kundenhistorie", style = MaterialTheme.typography.titleMedium) }
            item {
                Text("Gesamtumsatz (bezahlt): ${uiState.totalRevenueFormatted}", style = MaterialTheme.typography.bodyMedium)
            }
            items(uiState.openInvoices, key = { it.id }) { invoice ->
                Card(
                    onClick = { onInvoiceClick(invoice.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    ) {
                        Text(invoice.invoiceNumber)
                        Text(invoice.grossAmountFormatted)
                    }
                }
            }
        }

        item {
            Button(
                onClick = { onEvent(CustomerDetailEvent.Save) },
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (uiState.isSaving) "Wird gespeichert…" else "Speichern") }
        }
    }
}