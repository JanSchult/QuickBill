package com.example.myapplication.presentation.presentation.viewmodel.event

sealed interface SettingsEvent {
    data class InvoiceNumberPrefixChanged(val value: String) : SettingsEvent
    data class DefaultPaymentTermDaysChanged(val value: String) : SettingsEvent
    data class DefaultInvoiceNoteChanged(val value: String) : SettingsEvent
    data class SmallBusinessNoticeTextChanged(val value: String) : SettingsEvent
    data object Save : SettingsEvent
    data object DismissError : SettingsEvent
}