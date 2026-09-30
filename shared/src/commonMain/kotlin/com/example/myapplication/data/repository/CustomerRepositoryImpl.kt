package com.example.myapplication.data.repository


import com.example.myapplication.data.database.dao.CustomerDao
import com.example.myapplication.data.mapper.toDomain
import com.example.myapplication.data.mapper.toEntity
import com.example.myapplication.domain.model.Customer
import com.example.myapplication.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime

class CustomerRepositoryImpl(
    private val customerDao: CustomerDao
) : CustomerRepository {

    override fun observeActiveSortedByName(companyId: String): Flow<List<Customer>> {
        return customerDao.observeActiveSortedByName(companyId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeActiveSortedByLastUsed(companyId: String): Flow<List<Customer>> {
        return customerDao.observeActiveSortedByLastUsed(companyId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeArchived(companyId: String): Flow<List<Customer>> {
        return customerDao.observeArchived(companyId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun search(companyId: String, query: String): Flow<List<Customer>> {
        return customerDao.search(companyId, query).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeById(customerId: String): Flow<Customer?> {
        return customerDao.observeById(customerId).map { it?.toDomain() }
    }

    override suspend fun getById(customerId: String): Customer? {
        return customerDao.getById(customerId)?.toDomain()
    }

    override suspend fun insert(customer: Customer) {
        customerDao.insert(customer.toEntity())
    }

    override suspend fun update(customer: Customer) {
        customerDao.update(customer.toEntity())
    }

    override suspend fun archive(customerId: String, updatedAt: LocalDateTime) {
        customerDao.archive(customerId, updatedAt)
    }

    override suspend fun restore(customerId: String, updatedAt: LocalDateTime) {
        customerDao.restore(customerId, updatedAt)
    }

    override suspend fun touchLastUsed(customerId: String, usedAt: LocalDateTime) {
        customerDao.touchLastUsed(customerId, usedAt)
    }

    override suspend fun hasInvoices(customerId: String): Boolean {
        return customerDao.hasInvoices(customerId)
    }

    override suspend fun delete(customerId: String) {
        customerDao.delete(customerId)
    }
}