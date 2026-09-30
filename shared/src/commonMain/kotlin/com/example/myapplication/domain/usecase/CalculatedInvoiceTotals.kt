package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.InvoiceItem
import com.example.myapplication.domain.model.Money

data class CalculatedInvoiceTotals(
    val items: List<InvoiceItem>,
    val totalNet: Money,
    val totalTax: Money,
    val totalGross: Money
)