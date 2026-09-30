package com.example.myapplication.domain.repository


import com.example.myapplication.domain.model.InvoiceItem
import kotlinx.coroutines.flow.Flow

interface InvoiceItemRepository {
    fun observeForInvoice(invoiceId: String): Flow<List<InvoiceItem>>
    suspend fun getForInvoice(invoiceId: String): List<InvoiceItem>
    suspend fun insert(item: InvoiceItem)
    suspend fun insertAll(items: List<InvoiceItem>)
    suspend fun update(item: InvoiceItem)
    suspend fun delete(itemId: String)
    suspend fun deleteAllForInvoice(invoiceId: String)
}