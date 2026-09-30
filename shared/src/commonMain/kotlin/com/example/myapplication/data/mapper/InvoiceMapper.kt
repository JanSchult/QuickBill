package com.example.myapplication.data.mapper


import com.example.myapplication.data.database.entity.InvoiceEntity
import com.example.myapplication.domain.model.Invoice
import com.example.myapplication.domain.model.Money

fun InvoiceEntity.toDomain(): Invoice = Invoice(
    id = id,
    companyId = companyId,
    customerId = customerId,
    invoiceNumber = invoiceNumber,
    invoiceDate = invoiceDate,
    serviceDate = serviceDate,
    servicePeriodFrom = servicePeriodFrom,
    servicePeriodTo = servicePeriodTo,
    dueDate = dueDate,
    status = status,
    currency = currency,
    note = note,
    paymentTerms = paymentTerms,
    additionalHint = additionalHint,
    netAmount = Money(netAmountMinorUnits),
    taxAmount = Money(taxAmountMinorUnits),
    grossAmount = Money( grossAmountMinorUnits),
    createdAt = createdAt,
    updatedAt = updatedAt,
    paidAt = paidAt,
    cancelledAt = cancelledAt,
    pdfFileReference = pdfFileReference
)

fun Invoice.toEntity(): InvoiceEntity = InvoiceEntity(
    id = id,
    companyId = companyId,
    customerId = customerId,
    invoiceNumber = invoiceNumber,
    invoiceDate = invoiceDate,
    serviceDate = serviceDate,
    servicePeriodFrom = servicePeriodFrom,
    servicePeriodTo = servicePeriodTo,
    dueDate = dueDate,
    status = status,
    currency = currency,
    note = note,
    paymentTerms = paymentTerms,
    additionalHint = additionalHint,
    netAmountMinorUnits = netAmount.minorUnits,
    taxAmountMinorUnits = taxAmount.minorUnits,
    grossAmountMinorUnits = grossAmount.minorUnits,
    createdAt = createdAt,
    updatedAt = updatedAt,
    paidAt = paidAt,
    cancelledAt = cancelledAt,
    pdfFileReference = pdfFileReference
)