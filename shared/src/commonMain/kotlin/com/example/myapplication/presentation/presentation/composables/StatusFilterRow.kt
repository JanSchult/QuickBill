package com.example.myapplication.presentation.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.InvoiceStatus

@Composable
fun StatusFilterRow(
    selected: InvoiceStatus?,
    onSelect: (InvoiceStatus?) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        FilterChip(selected = selected == null, onClick = { onSelect(null) }, label = { Text("Alle") })
        FilterChip(selected = selected == InvoiceStatus.OPEN, onClick = { onSelect(InvoiceStatus.OPEN) }, label = { Text("Offen") })
        FilterChip(selected = selected == InvoiceStatus.OVERDUE, onClick = { onSelect(InvoiceStatus.OVERDUE) }, label = { Text("Überfällig") })
        FilterChip(selected = selected == InvoiceStatus.PAID, onClick = { onSelect(InvoiceStatus.PAID) }, label = { Text("Bezahlt") })
    }
}