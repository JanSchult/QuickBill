package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Customer
import com.example.myapplication.domain.repository.CustomerRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.domain.results.ValidationErrorItem
import com.example.myapplication.domain.validation.EmailValidator
import com.example.myapplication.util.AppClock


class UpdateCustomerUseCase(
    private val customerRepository: CustomerRepository,
    private val clock: AppClock
) {
    suspend operator fun invoke(customer: Customer): AppResult<Customer> {
        val errors = validate(customer)
        if (errors.isNotEmpty()) return AppResult.ValidationError(errors)

        val existing = customerRepository.getById(customer.id)
            ?: return AppResult.Failure(NoSuchElementException("Kunde nicht gefunden: ${customer.id}"))

        val updated = customer.copy(updatedAt = clock.nowDateTime(), createdAt = existing.createdAt)

        return try {
            customerRepository.update(updated)
            AppResult.Success(updated)
        } catch (e: Exception) {
            AppResult.Failure(e)
        }
    }

    private fun validate(customer: Customer): List<ValidationErrorItem> {
        val errors = mutableListOf<ValidationErrorItem>()
        val hasCompanyName = customer.companyName.isNotBlank()
        val hasPersonName = customer.firstName.isNotBlank() || customer.lastName.isNotBlank()
        if (!hasCompanyName && !hasPersonName) {
            errors += ValidationErrorItem("companyName", "Bitte Firmenname oder Vor-/Nachname angeben.")
        }
        if (customer.email.isNotBlank() && !EmailValidator.isValid(customer.email)) {
            errors += ValidationErrorItem("email", "Ungültige E-Mail-Adresse.")
        }
        return errors
    }
}