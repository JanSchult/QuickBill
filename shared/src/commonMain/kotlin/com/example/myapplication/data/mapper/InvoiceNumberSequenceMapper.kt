package com.example.myapplication.data.mapper


import com.example.myapplication.data.database.entity.InvoiceNumberSequenceEntity
import com.example.myapplication.domain.model.InvoiceNumberSequence

fun InvoiceNumberSequenceEntity.toDomain(): InvoiceNumberSequence = InvoiceNumberSequence(
    id = id,
    companyId = companyId,
    prefix = prefix,
    year = year,
    lastNumber = lastNumber
)

fun InvoiceNumberSequence.toEntity(): InvoiceNumberSequenceEntity = InvoiceNumberSequenceEntity(
    id = id,
    companyId = companyId,
    prefix = prefix,
    year = year,
    lastNumber = lastNumber
)