package com.example.myapplication.presentation.presentation.viewmodel.event

sealed interface CustomerDetailEvent {
    data class CustomerNumberChanged(val value: String) : CustomerDetailEvent
    data class CompanyNameChanged(val value: String) : CustomerDetailEvent
    data class FirstNameChanged(val value: String) : CustomerDetailEvent
    data class LastNameChanged(val value: String) : CustomerDetailEvent
    data class ContactPersonChanged(val value: String) : CustomerDetailEvent
    data class StreetChanged(val value: String) : CustomerDetailEvent
    data class HouseNumberChanged(val value: String) : CustomerDetailEvent
    data class PostalCodeChanged(val value: String) : CustomerDetailEvent
    data class CityChanged(val value: String) : CustomerDetailEvent
    data class CountryChanged(val value: String) : CustomerDetailEvent
    data class EmailChanged(val value: String) : CustomerDetailEvent
    data class PhoneChanged(val value: String) : CustomerDetailEvent
    data class VatIdChanged(val value: String) : CustomerDetailEvent
    data class NotesChanged(val value: String) : CustomerDetailEvent
    data class IsPrivateCustomerChanged(val value: Boolean) : CustomerDetailEvent

    data object Save : CustomerDetailEvent
    data object Duplicate : CustomerDetailEvent
    data object Archive : CustomerDetailEvent
    data object Restore : CustomerDetailEvent
    data object DismissError : CustomerDetailEvent
}