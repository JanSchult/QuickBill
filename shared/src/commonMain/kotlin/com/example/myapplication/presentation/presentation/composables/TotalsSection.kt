package com.example.myapplication.presentation.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.presentation.viewmodel.uimodel.InvoiceDraftUiModel

@Composable
internal fun TotalsSection(invoice: InvoiceDraftUiModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SummaryRow("Netto", invoice.netAmountFormatted)
            SummaryRow("Steuer", invoice.taxAmountFormatted)
            HorizontalDivider()
            SummaryRow("Brutto", invoice.grossAmountFormatted, emphasize = true)
            if (invoice.isSmallBusinessNoticeShown) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Gemäß § 19 UStG wird keine Umsatzsteuer berechnet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
