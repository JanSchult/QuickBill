package com.example.myapplication.data.mapper

// Datei: com/example/myapplication/data/mapper/CompanyMapper.kt

import com.example.myapplication.data.database.entity.CompanyEntity
import com.example.myapplication.domain.model.Company // Pfad deines Domain-Models

fun CompanyEntity.toDomain(): Company = Company(
    id = id,
    name = name,
    street = street,
    houseNumber = houseNumber,
    postalCode = postalCode,
    city = city,
    country = country,
    email = email,
    phone = phone,
    website = website,
    taxNumber = taxNumber,
    vatId = vatId,
    bankName = bankName,
    iban = iban,
    bic = bic,
    isSmallBusiness = isSmallBusiness,
    defaultCurrency = defaultCurrency,
    defaultPaymentTermDays = defaultPaymentTermDays,
    defaultInvoiceNote = defaultInvoiceNote,
    logo = logo,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Company.toEntity(): CompanyEntity = CompanyEntity(
    id = id,
    name = name,
    street = street,
    houseNumber = houseNumber,
    postalCode = postalCode,
    city = city,
    country = country,
    email = email,
    phone = phone,
    website = website,
    taxNumber = taxNumber,
    vatId = vatId,
    bankName = bankName,
    iban = iban,
    bic = bic,
    isSmallBusiness = isSmallBusiness,
    defaultCurrency = defaultCurrency,
    defaultPaymentTermDays = defaultPaymentTermDays,
    defaultInvoiceNote = defaultInvoiceNote,
    logo = logo,
    createdAt = createdAt,
    updatedAt = updatedAt
)