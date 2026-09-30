package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Company
import com.example.myapplication.domain.repository.CompanyRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.domain.results.ValidationErrorItem
import com.example.myapplication.util.AppClock
import com.example.myapplication.util.IdGenerator
import com.example.myapplication.domain.validation.EmailValidator
import com.example.myapplication.domain.validation.IbanValidator

class SaveCompanyProfileUseCase(
    private val companyRepository: CompanyRepository,
    private val clock: AppClock
) {
    suspend operator fun invoke(
        existingId: String?,
        name: String,
        street: String,
        houseNumber: String,
        postalCode: String,
        city: String,
        country: String,
        email: String,
        phone: String,
        website: String,
        taxNumber: String,
        vatId: String,
        bankName: String,
        iban: String,
        bic: String,
        isSmallBusiness: Boolean,
        defaultCurrency: String,
        defaultPaymentTermDays: Int,
        defaultInvoiceNote: String,
        logo: ByteArray?
    ): AppResult<Company> {
        val errors = validate(name, email, iban, defaultPaymentTermDays)
        if (errors.isNotEmpty()) return AppResult.ValidationError(errors)

        val now = clock.nowDateTime()
        val existing = existingId?.let { companyRepository.getCompany(it) }
        val company = Company(
            id = existingId ?: IdGenerator.newId(),
            name = name, street = street, houseNumber = houseNumber, postalCode = postalCode,
            city = city, country = country, email = email, phone = phone, website = website,
            taxNumber = taxNumber, vatId = vatId, bankName = bankName, iban = iban, bic = bic,
            isSmallBusiness = isSmallBusiness, defaultCurrency = defaultCurrency,
            defaultPaymentTermDays = defaultPaymentTermDays, defaultInvoiceNote = defaultInvoiceNote,
            logo = logo, createdAt = existing?.createdAt ?: now, updatedAt = now
        )

        return try {
            if (existing == null) companyRepository.saveCompany(company) else companyRepository.updateCompany(company)
            AppResult.Success(company)
        } catch (e: Exception) {
            AppResult.Failure(e)
        }
    }

    private fun validate(name: String, email: String, iban: String, paymentTermDays: Int): List<ValidationErrorItem> {
        val errors = mutableListOf<ValidationErrorItem>()
        if (name.isBlank()) errors += ValidationErrorItem("name", "Firmenname darf nicht leer sein.")
        if (name.length > 200) errors += ValidationErrorItem("name", "Firmenname ist zu lang (max. 200 Zeichen).")
        if (email.isNotBlank() && !EmailValidator.isValid(email)) errors += ValidationErrorItem("email", "Ungültige E-Mail-Adresse.")
        if (iban.isNotBlank() && !IbanValidator.isValid(iban)) errors += ValidationErrorItem("iban", "Ungültige IBAN.")
        if (paymentTermDays < 0) errors += ValidationErrorItem("defaultPaymentTermDays", "Zahlungsziel darf nicht negativ sein.")
        return errors
    }
}