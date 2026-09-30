package com.example.myapplication.data.mapper


import com.example.myapplication.data.database.entity.InvoiceItemEntity
import com.example.myapplication.domain.model.InvoiceItem
import com.example.myapplication.domain.model.Money
import com.example.myapplication.domain.model.TaxRate

fun InvoiceItemEntity.toDomain(): InvoiceItem = InvoiceItem(
    id = id,
    invoiceId = invoiceId,
    position = position,
    description = description,
    quantityThousandths = quantityThousandths,
    unit = unit,
    unitPriceNet = Money(unitPriceNetMinorUnits),        // ## korrigiert
    discountBasisPoints = discountBasisPoints,
    taxRate = TaxRate(taxRateBasisPoints),                 // ## korrigiert
    netAmount = Money(netAmountMinorUnits),               // ## korrigiert
    taxAmount = Money(taxAmountMinorUnits),                // ## korrigiert
    grossAmount = Money(grossAmountMinorUnits)             // ## korrigiert
)

fun InvoiceItem.toEntity(): InvoiceItemEntity = InvoiceItemEntity(
    id = id,
    invoiceId = invoiceId,
    position = position,
    description = description,
    quantityThousandths = quantityThousandths,
    unit = unit,
    unitPriceNetMinorUnits = unitPriceNet.minorUnits,     // ## korrigiert
    discountBasisPoints = discountBasisPoints,
    taxRateBasisPoints = taxRate.basisPoints,               // ## korrigiert
    netAmountMinorUnits = netAmount.minorUnits,            // ## korrigiert
    taxAmountMinorUnits = taxAmount.minorUnits,             // ## korrigiert
    grossAmountMinorUnits = grossAmount.minorUnits          // ## korrigiert
)