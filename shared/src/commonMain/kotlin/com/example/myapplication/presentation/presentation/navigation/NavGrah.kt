package com.example.myapplication.presentation.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.myapplication.domain.repository.AppSettingsRepository
import com.example.myapplication.presentation.presentation.composables.LoadingState
import com.example.myapplication.presentation.presentation.screens.CompanyProfileScreen
import com.example.myapplication.presentation.presentation.screens.CustomerDetailScreen
import com.example.myapplication.presentation.presentation.screens.CustomerListScreen
import com.example.myapplication.presentation.presentation.screens.DashboardScreen
import com.example.myapplication.presentation.presentation.screens.InvoiceListScreen
import com.example.myapplication.presentation.presentation.screens.InvoiceDetailScreen
import com.example.myapplication.presentation.presentation.screens.InvoiceEditorScreen
import com.example.myapplication.presentation.presentation.screens.SettingsScreen
import org.koin.compose.koinInject

/**
 * Lädt einmalig, welches Unternehmen aktiv ist (AppSettings.activeCompanyId), statt es
 * an jeder Route erneut aus der Datenbank zu ziehen oder über die Navigation zu reichen.
 * Solange keines existiert (Erststart der App), wird direkt zur Ersteinrichtung
 * geleitet - erst danach ergibt der restliche Navigationsgraph überhaupt Sinn.
 */
private sealed interface ActiveCompanyState {
    data object Loading : ActiveCompanyState
    data object NoCompany : ActiveCompanyState
    data class Ready(val companyId: String) : ActiveCompanyState
}

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    appSettingsRepository: AppSettingsRepository = koinInject()
) {
    val activeCompanyState by produceState<ActiveCompanyState>(ActiveCompanyState.Loading) {
        appSettingsRepository.observe().collect { settings ->
            value = settings?.activeCompanyId
                ?.let { ActiveCompanyState.Ready(it) }
                ?: ActiveCompanyState.NoCompany
        }
    }

    when (val state = activeCompanyState) {
        ActiveCompanyState.Loading -> LoadingState()

        ActiveCompanyState.NoCompany -> CompanyProfileScreen(
            companyId = null,
            onBack = { /* Ersteinrichtung - kein Zurück möglich, ohne Unternehmen läuft nichts */ },
            onSaved = { /* activeCompanyState aktualisiert sich automatisch über den Flow oben */ }
        )

        is ActiveCompanyState.Ready -> MainNavHost(navController = navController, activeCompanyId = state.companyId)
    }
}

@Composable
private fun MainNavHost(
    navController: NavHostController,
    activeCompanyId: String
) {
    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Invoices,
        BottomNavItem.Customers,
        BottomNavItem.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarVisible = items.any { item ->
        currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
    }

    Scaffold(
        bottomBar = {
            if (bottomBarVisible) {
                NavigationBar {
                    items.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any {
                            it.hasRoute(item.route::class)
                        } == true

                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo<Screens.Dashboard> { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screens.Dashboard,
            modifier = Modifier.padding(paddingValues)
        ) {
            // 1. Dashboard
            composable<Screens.Dashboard> {
                DashboardScreen(
                    onNewInvoiceClick = {
                        navController.navigate(Screens.InvoiceEditor(invoiceId = null))
                    },
                    onNewCustomerClick = {
                        navController.navigate(Screens.CustomerDetail(customerId = null))
                    },
                    onCompanyProfileClick = {
                        navController.navigate(Screens.CompanyProfile)
                    }
                )
            }

            // 2. Rechnungsliste
            composable<Screens.Invoices> {
                InvoiceListScreen(
                    onInvoiceClick = { invoiceId ->
                        navController.navigate(Screens.InvoiceDetail(invoiceId = invoiceId))
                    },
                    onNewInvoiceClick = {
                        navController.navigate(Screens.InvoiceEditor(invoiceId = null))
                    }
                )
            }

            // 3. Rechnungsdetails
            composable<Screens.InvoiceDetail> { backStackEntry ->
                val route: Screens.InvoiceDetail = backStackEntry.toRoute()
                InvoiceDetailScreen(
                    invoiceId = route.invoiceId,
                    onBack = { navController.popBackStack() },
                    onDuplicated = { newInvoiceId ->
                        navController.navigate(Screens.InvoiceEditor(invoiceId = newInvoiceId)) {
                            popUpTo<Screens.InvoiceDetail> { inclusive = true }
                        }
                    }
                )
            }

            // 4. Rechnung erstellen / bearbeiten
            composable<Screens.InvoiceEditor> { backStackEntry ->
                val route: Screens.InvoiceEditor = backStackEntry.toRoute()
                InvoiceEditorScreen(
                    invoiceId = route.invoiceId,
                    companyId = activeCompanyId,
                    onBack = { navController.popBackStack() },
                    onFinalized = { finalizedInvoiceId ->
                        navController.navigate(Screens.InvoiceDetail(invoiceId = finalizedInvoiceId)) {
                            popUpTo<Screens.InvoiceEditor> { inclusive = true }
                        }
                    }
                )
            }

            // 5. Kundenliste
            composable<Screens.Customers> {
                CustomerListScreen(
                    companyId = activeCompanyId,
                    onCustomerClick = { customerId ->
                        navController.navigate(Screens.CustomerDetail(customerId = customerId))
                    },
                    onNewCustomerClick = {
                        navController.navigate(Screens.CustomerDetail(customerId = null))
                    }
                )
            }

            // 6. Kunde erstellen / bearbeiten
            composable<Screens.CustomerDetail> { backStackEntry ->
                val route: Screens.CustomerDetail = backStackEntry.toRoute()
                CustomerDetailScreen(
                    customerId = route.customerId,
                    companyId = activeCompanyId,
                    onBack = { navController.popBackStack() },
                    onInvoiceClick = { invoiceId ->
                        navController.navigate(Screens.InvoiceDetail(invoiceId = invoiceId))
                    },
                    onDuplicated = { newCustomerId ->
                        navController.navigate(Screens.CustomerDetail(customerId = newCustomerId)) {
                            popUpTo<Screens.CustomerDetail> { inclusive = true }
                        }
                    }
                )
            }

            // 7. Unternehmensprofil
            composable<Screens.CompanyProfile> {
                CompanyProfileScreen(
                    companyId = activeCompanyId,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            // 8. Einstellungen
            composable<Screens.Settings> {
                SettingsScreen(
                    onCompanyProfileClick = { navController.navigate(Screens.CompanyProfile) },
                    onBackupClick = { /* folgt Phase 17 */ }
                )
            }
        }
    }
}