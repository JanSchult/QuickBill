package com.example.myapplication.presentation.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myapplication.domain.model.InvoiceStatus
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatusBadge(status: InvoiceStatus, modifier: Modifier = Modifier) {
    val (background, content, label) = when (status) {
        InvoiceStatus.DRAFT -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, "Entwurf")
        InvoiceStatus.OPEN -> Triple(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer, "Offen")
        InvoiceStatus.SENT -> Triple(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer, "Versendet")
        InvoiceStatus.PARTIALLY_PAID -> Triple(Color(0xFFFFF3CD), Color(0xFF8A6100), "Teilweise bezahlt")
        InvoiceStatus.PAID -> Triple(Color(0xFFD4EDDA), Color(0xFF1B5E20), "Bezahlt")
        InvoiceStatus.OVERDUE -> Triple(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer, "Überfällig")
        InvoiceStatus.CANCELLED -> Triple(MaterialTheme.colorScheme.outlineVariant, MaterialTheme.colorScheme.onSurface, "Storniert")
    }
    Text(
        text = label,
        color = content,
        fontSize = 12.sp,
        modifier = modifier
            .background(background, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}