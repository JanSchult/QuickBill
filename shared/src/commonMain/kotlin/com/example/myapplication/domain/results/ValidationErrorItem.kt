package com.example.myapplication.domain.results

data class ValidationErrorItem(
    val field: String,
    val message: String
)