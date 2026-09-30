package com.example.myapplication.data.mapper

import com.example.myapplication.data.database.entity.CustomerEntity
import com.example.myapplication.domain.model.Customer

fun CustomerEntity.toDomain(): Customer = Customer(
    id = id,
    companyId = companyId,
    customerNumber = customerNumber,
    companyName = companyName,
    firstName = firstName,
    lastName = lastName,
    contactPerson = contactPerson,
    street = street,
    houseNumber = houseNumber,
    postalCode = postalCode,
    city = city,
    country = country,
    email = email,
    phone = phone,
    vatId = vatId,
    defaultPaymentTermDays = defaultPaymentTermDays,
    notes = notes,
    isPrivateCustomer = isPrivateCustomer,
    isArchived = isArchived,
    createdAt = createdAt,
    updatedAt = updatedAt,
    lastUsedAt = lastUsedAt
)

fun Customer.toEntity(): CustomerEntity = CustomerEntity(
    id = id,
    companyId = companyId,
    customerNumber = customerNumber,
    companyName = companyName,
    firstName = firstName,
    lastName = lastName,
    contactPerson = contactPerson,
    street = street,
    houseNumber = houseNumber,
    postalCode = postalCode,
    city = city,
    country = country,
    email = email,
    phone = phone,
    vatId = vatId,
    defaultPaymentTermDays = defaultPaymentTermDays,
    notes = notes,
    isPrivateCustomer = isPrivateCustomer,
    isArchived = isArchived,
    createdAt = createdAt,
    updatedAt = updatedAt,
    lastUsedAt = lastUsedAt
)