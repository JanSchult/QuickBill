package com.example.myapplication.presentation.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.CustomerListItemUiModel

@Composable
 fun CustomerListRow(
    customer: CustomerListItemUiModel,
    onClick: () -> Unit,
    onArchive: () -> Unit,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(customer.displayName, style = MaterialTheme.typography.titleMedium)
                if (customer.email.isNotBlank()) {
                    Text(customer.email, style = MaterialTheme.typography.bodySmall)
                }
            }
            androidx.compose.foundation.layout.Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Weitere Aktionen")
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    if (customer.isArchived) {
                        DropdownMenuItem(text = { Text("Wiederherstellen") }, onClick = { menuExpanded = false; onRestore() })
                    } else {
                        DropdownMenuItem(text = { Text("Archivieren") }, onClick = { menuExpanded = false; onArchive() })
                    }
                    DropdownMenuItem(text = { Text("Löschen") }, onClick = { menuExpanded = false; onDelete() })
                }
            }
        }
    }
}