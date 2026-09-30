package com.example.myapplication.presentation.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.ItemUnit
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.InvoiceItemUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceItemEditorRow(
    item: InvoiceItemUiModel,
    onDescriptionChanged: (String) -> Unit,
    onQuantityChanged: (String) -> Unit,
    onUnitChanged: (ItemUnit) -> Unit,
    onUnitPriceChanged: (String) -> Unit,
    onDiscountChanged: (String) -> Unit,
    onTaxRateChanged: (String) -> Unit,
    onRemove: () -> Unit
) {
    var unitMenuExpanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Position ${item.position}", style = MaterialTheme.typography.labelLarge)
                IconButton(onClick = onRemove) {
                    Icon(Icons.Filled.Delete, contentDescription = "Position entfernen")
                }
            }

            OutlinedTextField(
                value = item.description,
                onValueChange = onDescriptionChanged,
                label = { Text("Beschreibung") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = item.quantityDisplay,
                    onValueChange = onQuantityChanged,
                    label = { Text("Menge") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )

                ExposedDropdownMenuBox(
                    expanded = unitMenuExpanded,
                    onExpandedChange = { unitMenuExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = item.unit.displayLabel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Einheit") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitMenuExpanded) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(expanded = unitMenuExpanded, onDismissRequest = { unitMenuExpanded = false }) {
                        ItemUnit.entries.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(unit.displayLabel) },
                                onClick = {
                                    onUnitChanged(unit)
                                    unitMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = item.unitPriceDisplay,
                    onValueChange = onUnitPriceChanged,
                    label = { Text("Einzelpreis (€)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = item.discountDisplay,
                    onValueChange = onDiscountChanged,
                    label = { Text("Rabatt (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = item.taxRateDisplay,
                    onValueChange = onTaxRateChanged,
                    label = { Text("Steuersatz (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider()

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Netto: ${item.netAmountFormatted}", style = MaterialTheme.typography.bodySmall)
                Text("Steuer: ${item.taxAmountFormatted}", style = MaterialTheme.typography.bodySmall)
                Text("Brutto: ${item.grossAmountFormatted}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}