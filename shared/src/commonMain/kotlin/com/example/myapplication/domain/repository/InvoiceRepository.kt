package com.example.myapplication.domain.repository


import com.example.myapplication.domain.model.Invoice
import com.example.myapplication.domain.model.InvoiceStatus
import com.example.myapplication.domain.model.InvoiceWithItems
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface InvoiceRepository {
    fun observeAll(companyId: String): Flow<List<Invoice>>
    fun observeByStatus(companyId: String, status: InvoiceStatus): Flow<List<Invoice>>
    fun observeByCustomer(customerId: String): Flow<List<Invoice>>
    fun observeOpenInvoices(companyId: String): Flow<List<Invoice>>
    fun observeOverdueInvoices(companyId: String, today: LocalDate): Flow<List<Invoice>>
    fun search(companyId: String, query: String): Flow<List<Invoice>>
    fun observeById(invoiceId: String): Flow<Invoice?>
    fun observeWithItem(invoiceID: String) : Flow<InvoiceWithItems?>
    suspend fun getById(invoiceId: String): Invoice?
    suspend fun existsByNumber(companyId: String, invoiceNumber: String): Boolean
    suspend fun insert(invoice: Invoice)
    suspend fun update(invoice: Invoice)
    suspend fun deleteDraft(invoiceId: String): Boolean
}