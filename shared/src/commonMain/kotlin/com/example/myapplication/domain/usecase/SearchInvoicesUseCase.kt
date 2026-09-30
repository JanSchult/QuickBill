package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Invoice
import com.example.myapplication.domain.repository.InvoiceRepository
import kotlinx.coroutines.flow.Flow

class SearchInvoicesUseCase(
    private val invoiceRepository: InvoiceRepository
) {
    operator fun invoke(companyId: String, query: String): Flow<List<Invoice>> =
        if (query.isBlank()) invoiceRepository.observeAll(companyId)
        else invoiceRepository.search(companyId, query)
}