package com.example.myapplication.presentation.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.presentation.viewmodel.event.CompanyProfileEvent
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.CompanyProfileFormUiModel
import com.example.myapplication.presentation.presentation.viewmodel.uistate.CompanyProfileUiState

@Composable
 fun CompanyProfileContent(
    uiState: CompanyProfileUiState,
    form: CompanyProfileFormUiModel,
    padding: PaddingValues,
    onEvent: (CompanyProfileEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Firmendaten", style = MaterialTheme.typography.titleMedium) }

        item {
            OutlinedTextField(
                value = form.name,
                onValueChange = { onEvent(CompanyProfileEvent.NameChanged(it)) },
                label = { Text("Firmenname / vollständiger Name") },
                isError = uiState.validationErrors.containsKey("name"),
                supportingText = uiState.validationErrors["name"]?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item { Text("Adresse", style = MaterialTheme.typography.titleMedium) }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = form.street,
                    onValueChange = { onEvent(CompanyProfileEvent.StreetChanged(it)) },
                    label = { Text("Straße") },
                    modifier = Modifier.weight(2f)
                )
                OutlinedTextField(
                    value = form.houseNumber,
                    onValueChange = { onEvent(CompanyProfileEvent.HouseNumberChanged(it)) },
                    label = { Text("Nr.") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = form.postalCode,
                    onValueChange = { onEvent(CompanyProfileEvent.PostalCodeChanged(it)) },
                    label = { Text("PLZ") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = form.city,
                    onValueChange = { onEvent(CompanyProfileEvent.CityChanged(it)) },
                    label = { Text("Ort") },
                    modifier = Modifier.weight(2f)
                )
            }
        }

        item {
            OutlinedTextField(
                value = form.country,
                onValueChange = { onEvent(CompanyProfileEvent.CountryChanged(it)) },
                label = { Text("Land") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item { Text("Kontakt", style = MaterialTheme.typography.titleMedium) }

        item {
            OutlinedTextField(
                value = form.email,
                onValueChange = { onEvent(CompanyProfileEvent.EmailChanged(it)) },
                label = { Text("E-Mail") },
                isError = uiState.validationErrors.containsKey("email"),
                supportingText = uiState.validationErrors["email"]?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = form.phone,
                onValueChange = { onEvent(CompanyProfileEvent.PhoneChanged(it)) },
                label = { Text("Telefonnummer") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = form.website,
                onValueChange = { onEvent(CompanyProfileEvent.WebsiteChanged(it)) },
                label = { Text("Website") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item { Text("Steuerliche Angaben", style = MaterialTheme.typography.titleMedium) }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = form.taxNumber,
                    onValueChange = { onEvent(CompanyProfileEvent.TaxNumberChanged(it)) },
                    label = { Text("Steuernummer") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = form.vatId,
                    onValueChange = { onEvent(CompanyProfileEvent.VatIdChanged(it)) },
                    label = { Text("USt-ID") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Checkbox(
                    checked = form.isSmallBusiness,
                    onCheckedChange = { onEvent(CompanyProfileEvent.IsSmallBusinessChanged(it)) }
                )
                Column {
                    Text("Kleinunternehmer nach § 19 UStG", modifier = Modifier.padding(top = 12.dp))
                }
            }
        }

        item { Text("Bankverbindung", style = MaterialTheme.typography.titleMedium) }

        item {
            OutlinedTextField(
                value = form.bankName,
                onValueChange = { onEvent(CompanyProfileEvent.BankNameChanged(it)) },
                label = { Text("Bankname") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = form.iban,
                    onValueChange = { onEvent(CompanyProfileEvent.IbanChanged(it)) },
                    label = { Text("IBAN") },
                    isError = uiState.validationErrors.containsKey("iban"),
                    supportingText = uiState.validationErrors["iban"]?.let { { Text(it) } },
                    modifier = Modifier.weight(2f)
                )
                OutlinedTextField(
                    value = form.bic,
                    onValueChange = { onEvent(CompanyProfileEvent.BicChanged(it)) },
                    label = { Text("BIC") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item { Text("Standardwerte für neue Rechnungen", style = MaterialTheme.typography.titleMedium) }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = form.defaultCurrency,
                    onValueChange = { onEvent(CompanyProfileEvent.DefaultCurrencyChanged(it)) },
                    label = { Text("Standardwährung") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = form.defaultPaymentTermDays,
                    onValueChange = { onEvent(CompanyProfileEvent.DefaultPaymentTermDaysChanged(it)) },
                    label = { Text("Zahlungsziel (Tage)") },
                    isError = uiState.validationErrors.containsKey("defaultPaymentTermDays"),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            OutlinedTextField(
                value = form.defaultInvoiceNote,
                onValueChange = { onEvent(CompanyProfileEvent.DefaultInvoiceNoteChanged(it)) },
                label = { Text("Standardhinweis auf Rechnungen") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Logo-Upload braucht eine plattformspezifische Dateiauswahl (expect/actual) -
        // bewusst zurückgestellt bis Phase 6, wo die PDF-Abstraktion ohnehin Dateizugriffe
        // definiert. Hier nur ein Platzhalter, damit das Feld sichtbar und nicht vergessen ist.
        item {
            Text(
                text = if (form.logo != null) "Logo hinterlegt" else "Kein Logo hinterlegt (Auswahl folgt in Phase 6)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Button(
                onClick = { onEvent(CompanyProfileEvent.Save) },
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (uiState.isSaving) "Wird gespeichert…" else "Speichern") }
        }
    }
}