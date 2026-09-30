package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Customer
import com.example.myapplication.domain.repository.CustomerRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.domain.results.ValidationErrorItem
import com.example.myapplication.domain.validation.EmailValidator
import com.example.myapplication.util.AppClock
import com.example.myapplication.util.IdGenerator

class CreateCustomerUseCase(
    private val customerRepository: CustomerRepository,
    private val clock: AppClock
) {
    suspend operator fun invoke(
        companyId: String,
        customerNumber: String,
        companyName: String,
        firstName: String,
        lastName: String,
        contactPerson: String,
        street: String,
        houseNumber: String,
        postalCode: String,
        city: String,
        country: String,
        email: String,
        phone: String,
        vatId: String,
        defaultPaymentTermDays: Int?,
        notes: String,
        isPrivateCustomer: Boolean
    ): AppResult<Customer> {
        val errors = validate(companyName, firstName, lastName, email)
        if (errors.isNotEmpty()) return AppResult.ValidationError(errors)

        val now = clock.nowDateTime()
        val customer = Customer(
            id = IdGenerator.newId(),
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
            isArchived = false,
            createdAt = now,
            updatedAt = now,
            lastUsedAt = null
        )

        return try {
            customerRepository.insert(customer)
            AppResult.Success(customer)
        } catch (e: Exception) {
            AppResult.Failure(e)
        }
    }

    private fun validate(
        companyName: String,
        firstName: String,
        lastName: String,
        email: String
    ): List<ValidationErrorItem> {
        val errors = mutableListOf<ValidationErrorItem>()

        // Ein Kunde muss entweder Firmennamen oder Namen besitzen (Phase 3-Vorgabe).
        val hasCompanyName = companyName.isNotBlank()
        val hasPersonName = firstName.isNotBlank() || lastName.isNotBlank()
        if (!hasCompanyName && !hasPersonName) {
            errors += ValidationErrorItem("companyName", "Bitte Firmenname oder Vor-/Nachname angeben.")
        }
        if (email.isNotBlank() && !EmailValidator.isValid(email)) {
            errors += ValidationErrorItem("email", "Ungültige E-Mail-Adresse.")
        }
        return errors
    }
}