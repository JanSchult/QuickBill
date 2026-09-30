package com.example.myapplication.domain.model

data class InvoiceWithItems(
    val invoice: Invoice,
    val items: List<InvoiceItem>
)