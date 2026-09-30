package com.example.myapplication.domain.model


import kotlinx.datetime.LocalDateTime

data class Customer(
    val id: String,
    val companyId: String,
    val customerNumber: String,
    val companyName: String,
    val firstName: String,
    val lastName: String,
    val contactPerson: String,
    val street: String,
    val houseNumber: String,
    val postalCode: String,
    val city: String,
    val country: String,
    val email: String,
    val phone: String,
    val vatId: String,
    val defaultPaymentTermDays: Int?,
    val notes: String,
    val isPrivateCustomer: Boolean,
    val isArchived: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val lastUsedAt: LocalDateTime?
)