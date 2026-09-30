package com.example.myapplication.data.repository


import com.example.myapplication.data.database.dao.InvoiceDao
import com.example.myapplication.data.mapper.toDomain
import com.example.myapplication.data.mapper.toEntity
import com.example.myapplication.domain.model.Invoice
import com.example.myapplication.domain.model.InvoiceStatus
import com.example.myapplication.domain.model.InvoiceWithItems
import com.example.myapplication.domain.repository.InvoiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class InvoiceRepositoryImpl(
    private val invoiceDao: InvoiceDao
) : InvoiceRepository {

    override fun observeAll(companyId: String): Flow<List<Invoice>> {
        return invoiceDao.observeAll(companyId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeByStatus(companyId: String, status: InvoiceStatus): Flow<List<Invoice>> {
        return invoiceDao.observeByStatus(companyId, status).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeByCustomer(customerId: String): Flow<List<Invoice>> {
        return invoiceDao.observeByCustomer(customerId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeOpenInvoices(companyId: String): Flow<List<Invoice>> {
        return invoiceDao.observeOpenInvoices(companyId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeOverdueInvoices(companyId: String, today: LocalDate): Flow<List<Invoice>> {
        return invoiceDao.observeOverdueInvoices(companyId, today).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun search(companyId: String, query: String): Flow<List<Invoice>> {
        return invoiceDao.search(companyId, query).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeById(invoiceId: String): Flow<Invoice?> {
        return invoiceDao.observeById(invoiceId).map { it?.toDomain() }
    }

    override fun observeWithItem(invoiceID: String): Flow<InvoiceWithItems?> =
        invoiceDao.observeWithItems(invoiceID).map { it?.let {relation ->
            InvoiceWithItems(relation.invoice.toDomain(), relation.items.map { it.toDomain() })
        } }

    override suspend fun getById(invoiceId: String): Invoice? {
        return invoiceDao.getById(invoiceId)?.toDomain()
    }

    override suspend fun existsByNumber(companyId: String, invoiceNumber: String): Boolean {
        return invoiceDao.existsByNumber(companyId, invoiceNumber)
    }

    override suspend fun insert(invoice: Invoice) {
        invoiceDao.insert(invoice.toEntity())
    }

    override suspend fun update(invoice: Invoice) {
        invoiceDao.update(invoice.toEntity())
    }

    override suspend fun deleteDraft(invoiceId: String): Boolean {
        val deletedRows = invoiceDao.deleteDraft(invoiceId)
        return deletedRows > 0
    }
}