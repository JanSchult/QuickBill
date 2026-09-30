package com.example.myapplication.presentation.presentation.viewmodel.uimodel

data class CustomerFormUiModel(
    val id: String,
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
    val notes: String,
    val isPrivateCustomer: Boolean,
    val isArchived: Boolean
)