package com.example.myapplication.presentation.presentation.viewmodel.uimodel

data class CompanyProfileFormUiModel(
    val id: String?,
    val name: String,
    val street: String,
    val houseNumber: String,
    val postalCode: String,
    val city: String,
    val country: String,
    val email: String,
    val phone: String,
    val website: String,
    val taxNumber: String,
    val vatId: String,
    val bankName: String,
    val iban: String,
    val bic: String,
    val isSmallBusiness: Boolean,
    val defaultCurrency: String,
    val defaultPaymentTermDays: String,   // String fürs Textfeld, Parsing beim Speichern
    val defaultInvoiceNote: String,
    val logo: ByteArray?   // Referenzielle equals() hier unproblematisch - reiner Render-State, anders als CompanyEntity
)
