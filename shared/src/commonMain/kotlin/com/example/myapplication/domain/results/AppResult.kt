package com.example.myapplication.domain.results

sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class ValidationError(val errors: List<ValidationErrorItem>) : AppResult<Nothing>
    data class Failure(val cause: Throwable) : AppResult<Nothing>
}
