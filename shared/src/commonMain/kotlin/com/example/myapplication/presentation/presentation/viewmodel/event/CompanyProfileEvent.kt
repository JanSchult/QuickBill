package com.example.myapplication.presentation.presentation.viewmodel.event

sealed interface CompanyProfileEvent {
    data class NameChanged(val value: String) : CompanyProfileEvent
    data class StreetChanged(val value: String) : CompanyProfileEvent
    data class HouseNumberChanged(val value: String) : CompanyProfileEvent
    data class PostalCodeChanged(val value: String) : CompanyProfileEvent
    data class CityChanged(val value: String) : CompanyProfileEvent
    data class CountryChanged(val value: String) : CompanyProfileEvent
    data class EmailChanged(val value: String) : CompanyProfileEvent
    data class PhoneChanged(val value: String) : CompanyProfileEvent
    data class WebsiteChanged(val value: String) : CompanyProfileEvent
    data class TaxNumberChanged(val value: String) : CompanyProfileEvent
    data class VatIdChanged(val value: String) : CompanyProfileEvent
    data class BankNameChanged(val value: String) : CompanyProfileEvent
    data class IbanChanged(val value: String) : CompanyProfileEvent
    data class BicChanged(val value: String) : CompanyProfileEvent
    data class IsSmallBusinessChanged(val value: Boolean) : CompanyProfileEvent
    data class DefaultCurrencyChanged(val value: String) : CompanyProfileEvent
    data class DefaultPaymentTermDaysChanged(val value: String) : CompanyProfileEvent
    data class DefaultInvoiceNoteChanged(val value: String) : CompanyProfileEvent
    data class LogoChanged(val bytes: ByteArray?) : CompanyProfileEvent
    data object Save : CompanyProfileEvent
    data object DismissError : CompanyProfileEvent
}