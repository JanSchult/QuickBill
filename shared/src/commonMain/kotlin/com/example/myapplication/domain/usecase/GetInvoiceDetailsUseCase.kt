package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.InvoiceWithItems
import com.example.myapplication.domain.repository.InvoiceRepository
import kotlinx.coroutines.flow.Flow

class GetInvoiceDetailsUseCase(
    private val invoiceRepository: InvoiceRepository
) {
    operator fun invoke(invoiceId: String): Flow<InvoiceWithItems?> =
        invoiceRepository.observeWithItem(invoiceId)
}