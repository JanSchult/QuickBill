package com.example.myapplication.domain.repository


import com.example.myapplication.domain.model.Customer
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

interface CustomerRepository {
    fun observeActiveSortedByName(companyId: String): Flow<List<Customer>>
    fun observeActiveSortedByLastUsed(companyId: String): Flow<List<Customer>>
    fun observeArchived(companyId: String): Flow<List<Customer>>
    fun search(companyId: String, query: String): Flow<List<Customer>>
    fun observeById(customerId: String): Flow<Customer?>
    suspend fun getById(customerId: String): Customer?
    suspend fun insert(customer: Customer)
    suspend fun update(customer: Customer)
    suspend fun archive(customerId: String, updatedAt: LocalDateTime)
    suspend fun restore(customerId: String, updatedAt: LocalDateTime)
    suspend fun touchLastUsed(customerId: String, usedAt: LocalDateTime)
    suspend fun hasInvoices(customerId: String): Boolean
    suspend fun delete(customerId: String)
}