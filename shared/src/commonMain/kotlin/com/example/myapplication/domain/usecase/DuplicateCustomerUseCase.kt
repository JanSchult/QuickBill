package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Customer
import com.example.myapplication.domain.repository.CustomerRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.util.AppClock
import com.example.myapplication.util.IdGenerator

class DuplicateCustomerUseCase(
    private val customerRepository: CustomerRepository,
    private val clock: AppClock
) {
    suspend operator fun invoke(sourceCustomerId: String): AppResult<Customer> {
        val source = customerRepository.getById(sourceCustomerId)
            ?: return AppResult.Failure(NoSuchElementException("Kunde nicht gefunden: $sourceCustomerId"))

        val now = clock.nowDateTime()
        val copy = source.copy(
            id = IdGenerator.newId(),
            customerNumber = "",   // muss manuell neu vergeben werden - keine geteilten Kundennummern
            isArchived = false,
            createdAt = now,
            updatedAt = now,
            lastUsedAt = null
        )

        return try {
            customerRepository.insert(copy)
            AppResult.Success(copy)
        } catch (e: Exception) {
            AppResult.Failure(e)
        }
    }
}