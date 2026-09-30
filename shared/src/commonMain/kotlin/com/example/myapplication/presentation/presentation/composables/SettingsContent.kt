package com.example.myapplication.presentation.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.presentation.viewmodel.event.SettingsEvent
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.SettingsFormUiModel
import com.example.myapplication.presentation.presentation.viewmodel.uistate.SettingsUiState

@Composable
 fun SettingsContent(
    uiState: SettingsUiState,
    form: SettingsFormUiModel,
    padding: PaddingValues,
    onEvent: (SettingsEvent) -> Unit,
    onCompanyProfileClick: () -> Unit,
    onBackupClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SettingsSectionCard(
                title = "Unternehmensprofil",
                description = uiState.activeCompanyName ?: "Kein Unternehmen ausgewählt",
                onClick = onCompanyProfileClick
            )
        }

        item { Text("Rechnungsnummern", style = MaterialTheme.typography.titleMedium) }

        item {
            OutlinedTextField(
                value = form.invoiceNumberPrefix,
                onValueChange = { onEvent(SettingsEvent.InvoiceNumberPrefixChanged(it)) },
                label = { Text("Präfix (z. B. RE, INV)") },
                isError = uiState.validationErrors.containsKey("invoiceNumberPrefix"),
                supportingText = uiState.validationErrors["invoiceNumberPrefix"]?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item { Text("Standardwerte", style = MaterialTheme.typography.titleMedium) }

        item {
            OutlinedTextField(
                value = form.defaultPaymentTermDays,
                onValueChange = { onEvent(SettingsEvent.DefaultPaymentTermDaysChanged(it)) },
                label = { Text("Standard-Zahlungsziel (Tage)") },
                isError = uiState.validationErrors.containsKey("defaultPaymentTermDays"),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = form.defaultInvoiceNote,
                onValueChange = { onEvent(SettingsEvent.DefaultInvoiceNoteChanged(it)) },
                label = { Text("Standardhinweis auf Rechnungen") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item { Text("Steueroptionen", style = MaterialTheme.typography.titleMedium) }

        item {
            OutlinedTextField(
                value = form.smallBusinessNoticeText,
                onValueChange = { onEvent(SettingsEvent.SmallBusinessNoticeTextChanged(it)) },
                label = { Text("Hinweistext für Kleinunternehmer (§ 19 UStG)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text(
                text = "Diese App stellt keine verbindliche Steuerberatung dar. Bitte lass die steuerliche Einordnung im Zweifel von einem Steuerberater prüfen.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item { Text("Datensicherung", style = MaterialTheme.typography.titleMedium) }

        item {
            // Export/Backup-Logik folgt mit Phase 17 (Datenschutz/Datenexport) - Platzhalter,
            // damit der Menüpunkt bereits sichtbar und die Navigation vorbereitet ist.
            OutlinedButton(onClick = onBackupClick, enabled = false, modifier = Modifier.fillMaxWidth()) {
                Text("Daten exportieren (folgt)")
            }
        }

        item {
            Button(
                onClick = { onEvent(SettingsEvent.Save) },
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (uiState.isSaving) "Wird gespeichert…" else "Speichern") }
        }
    }
}