package com.example.myapplication.data.repository


import com.example.myapplication.data.database.dao.InvoiceItemDao
import com.example.myapplication.data.mapper.toDomain
import com.example.myapplication.data.mapper.toEntity
import com.example.myapplication.domain.model.InvoiceItem
import com.example.myapplication.domain.repository.InvoiceItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InvoiceItemRepositoryImpl(
    private val invoiceItemDao: InvoiceItemDao
) : InvoiceItemRepository {

    override fun observeForInvoice(invoiceId: String): Flow<List<InvoiceItem>> {
        return invoiceItemDao.observeForInvoice(invoiceId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getForInvoice(invoiceId: String): List<InvoiceItem> {
        return invoiceItemDao.getForInvoice(invoiceId).map { it.toDomain() }
    }

    override suspend fun insert(item: InvoiceItem) {
        invoiceItemDao.insert(item.toEntity())
    }

    override suspend fun insertAll(items: List<InvoiceItem>) {
        invoiceItemDao.insertAll(items.map { it.toEntity() })
    }

    override suspend fun update(item: InvoiceItem) {
        invoiceItemDao.update(item.toEntity())
    }

    override suspend fun delete(itemId: String) {
        invoiceItemDao.delete(itemId)
    }

    override suspend fun deleteAllForInvoice(invoiceId: String) {
        invoiceItemDao.deleteAllForInvoice(invoiceId)
    }
}