package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.repository.CustomerRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.util.AppClock

/**
 * Löscht einen Kunden nur, wenn keine Rechnungen existieren (FK RESTRICT, Phase 4a).
 * Existieren Rechnungen, wird stattdessen archiviert, um die Nachvollziehbarkeit
 * zu erhalten - kein stiller Fehlschlag, sondern bewusstes Fallback-Verhalten.
 */
class DeleteCustomerUseCase(
    private val customerRepository: CustomerRepository,
    private val clock: AppClock
) {
    suspend operator fun invoke(customerId: String): AppResult<Unit> {
        return try {
            if (customerRepository.hasInvoices(customerId)) {
                customerRepository.archive(customerId, clock.nowDateTime())
            } else {
                customerRepository.delete(customerId)
            }
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Failure(e)
        }
    }
}