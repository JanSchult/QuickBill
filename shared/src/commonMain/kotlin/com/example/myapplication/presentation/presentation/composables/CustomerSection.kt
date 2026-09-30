package com.example.myapplication.presentation.presentation.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.CustomerUiModel
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
 fun CustomerSection(
    customers: List<CustomerUiModel>,
    selectedCustomerId: String?,
    error: String?,
    onCustomerSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = customers.firstOrNull { it.id == selectedCustomerId }?.displayName ?: ""

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Kunde") },
            isError = error != null,
            supportingText = error?.let { { Text(it) } },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            customers.forEach { customer ->
                DropdownMenuItem(
                    text = { Text(customer.displayName) },
                    onClick = {
                        onCustomerSelected(customer.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
