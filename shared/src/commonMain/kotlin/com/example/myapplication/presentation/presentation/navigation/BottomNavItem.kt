package com.example.myapplication.presentation.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: Screens,
    val title: String,
    val icon: ImageVector
) {
    data object Dashboard : BottomNavItem(
        route = Screens.Dashboard,
        title = "Dashboard",
        icon = Icons.Default.Dashboard
    )

    data object Invoices : BottomNavItem(
        route = Screens.Invoices,
        title = "Rechnungen",
        icon = Icons.Default.Receipt
    )

    data object Customers : BottomNavItem(
        route = Screens.Customers,
        title = "Kunden",
        icon = Icons.Default.People
    )

    data object Settings : BottomNavItem(   // ## geändert: war CompanyProfile
        route = Screens.Settings,
        title = "Einstellungen",
        icon = Icons.Default.Settings
    )
}