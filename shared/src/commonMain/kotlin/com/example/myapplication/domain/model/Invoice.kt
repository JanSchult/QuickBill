package com.example.myapplication.domain.model


import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class Invoice(
    val id: String,
    val companyId: String,
    val customerId: String?,
    val invoiceNumber: String,
    val invoiceDate: LocalDate,
    val serviceDate: LocalDate?,
    val servicePeriodFrom: LocalDate?,
    val servicePeriodTo: LocalDate?,
    val dueDate: LocalDate,
    val status: InvoiceStatus,
    val currency: String,
    val note: String,
    val paymentTerms: String,
    val additionalHint: String,
    val netAmount: Money,
    val taxAmount: Money,
    val grossAmount: Money,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val paidAt: LocalDateTime?,
    val cancelledAt: LocalDateTime?,
    val pdfFileReference: String?
)