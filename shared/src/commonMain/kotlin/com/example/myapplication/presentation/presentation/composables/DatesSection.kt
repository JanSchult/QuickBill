package com.example.myapplication.presentation.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.InvoiceDraftUiModel
import kotlinx.datetime.LocalDate

@Composable
internal fun DatesSection(
    invoice: InvoiceDraftUiModel,
    errors: Map<String, String>,
    onInvoiceDateChanged: (LocalDate) -> Unit,
    onServiceDateChanged: (LocalDate?) -> Unit,
    onDueDateChanged: (LocalDate) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppDatePickerField(
            label = "Rechnungsdatum",
            displayValue = invoice.invoiceDateDisplay,
            currentDate = invoice.invoiceDate,
            onDateSelected = onInvoiceDateChanged,
            isError = errors.containsKey("invoiceDate"),
            modifier = Modifier.fillMaxWidth()
        )
        AppDatePickerField(
            label = "Leistungsdatum",
            displayValue = invoice.serviceDateDisplay,
            currentDate = invoice.serviceDate,
            onDateSelected = onServiceDateChanged,
            modifier = Modifier.fillMaxWidth()
        )
        AppDatePickerField(
            label = "Fälligkeitsdatum",
            displayValue = invoice.dueDateDisplay,
            currentDate = invoice.dueDate,
            onDateSelected = onDueDateChanged,
            isError = errors.containsKey("dueDate"),
            supportingText = errors["dueDate"],
            modifier = Modifier.fillMaxWidth()
        )
    }
}
