package com.example.myapplication.domain.validation

object EmailValidator {
    private val EMAIL_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    fun isValid(email: String): Boolean = EMAIL_REGEX.matches(email)
}