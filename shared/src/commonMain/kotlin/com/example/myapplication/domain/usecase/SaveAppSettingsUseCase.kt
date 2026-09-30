package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.AppSettings
import com.example.myapplication.domain.repository.AppSettingsRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.domain.results.ValidationErrorItem

class SaveAppSettingsUseCase(
    private val appSettingsRepository: AppSettingsRepository
) {
    suspend operator fun invoke(settings: AppSettings): AppResult<AppSettings> {
        val errors = mutableListOf<ValidationErrorItem>()
        if (settings.invoiceNumberPrefix.isBlank()) errors += ValidationErrorItem("invoiceNumberPrefix", "Präfix darf nicht leer sein.")
        if (settings.invoiceNumberPrefix.length > 20) errors += ValidationErrorItem("invoiceNumberPrefix", "Präfix ist zu lang (max. 20 Zeichen).")
        if (settings.defaultPaymentTermDays < 0) errors += ValidationErrorItem("defaultPaymentTermDays", "Zahlungsziel darf nicht negativ sein.")
        if (errors.isNotEmpty()) return AppResult.ValidationError(errors)

        return try {
            appSettingsRepository.save(settings)
            AppResult.Success(settings)
        } catch (e: Exception) {
            AppResult.Failure(e)
        }
    }
}