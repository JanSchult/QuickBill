package com.example.myapplication.domain.model


data class InvoiceNumberSequence(
    val id: String,
    val companyId: String,
    val prefix: String,
    val year: Int,
    val lastNumber: Int
)