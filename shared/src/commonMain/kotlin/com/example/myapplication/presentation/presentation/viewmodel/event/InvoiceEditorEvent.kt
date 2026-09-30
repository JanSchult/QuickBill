package com.example.myapplication.presentation.presentation.viewmodel.event

import com.example.myapplication.domain.model.ItemUnit
import kotlinx.datetime.LocalDate

sealed interface InvoiceEditorEvent {
    data class CustomerSelected(val customerId: String) : InvoiceEditorEvent
    data class InvoiceDateChanged(val date: LocalDate) : InvoiceEditorEvent
    data class DueDateChanged(val date: LocalDate) : InvoiceEditorEvent
    data class ServiceDateChanged(val date: LocalDate?) : InvoiceEditorEvent
    data class PaymentTermsChanged(val value: String) : InvoiceEditorEvent
    data class NoteChanged(val value: String) : InvoiceEditorEvent
    data class AdditionalHintChanged(val value: String) : InvoiceEditorEvent

    data object AddItem : InvoiceEditorEvent
    data class DescriptionChanged(val itemId: String, val value: String) : InvoiceEditorEvent
    data class QuantityChanged(val itemId: String, val value: String) : InvoiceEditorEvent
    data class UnitChanged(val itemId: String, val unit: ItemUnit) : InvoiceEditorEvent
    data class UnitPriceChanged(val itemId: String, val value: String) : InvoiceEditorEvent
    data class DiscountChanged(val itemId: String, val value: String) : InvoiceEditorEvent
    data class TaxRateChanged(val itemId: String, val value: String) : InvoiceEditorEvent
    data class RemoveItem(val itemId: String) : InvoiceEditorEvent

    data object SaveDraft : InvoiceEditorEvent
    data object FinalizeInvoice : InvoiceEditorEvent
    data object DismissError : InvoiceEditorEvent
}