package com.example.myapplication.presentation.presentation.navigation

import kotlinx.serialization.Serializable


sealed interface Screens {
    @Serializable
    data object Dashboard : Screens

    @Serializable
    data object Invoices : Screens

    @Serializable
    data class InvoiceDetail(val invoiceId: String) : Screens

    @Serializable
    data class InvoiceEditor(val invoiceId: String? = null) : Screens   // ## companyId entfernt - kommt zentral aus MainScreen

    @Serializable
    data object Customers : Screens

    @Serializable
    data class CustomerDetail(val customerId: String? = null) : Screens   // ## ergänzt

    @Serializable
    data object CompanyProfile : Screens

    @Serializable
    data object Settings : Screens   // ## ergänzt
}