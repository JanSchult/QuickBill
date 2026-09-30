package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Company
import kotlinx.coroutines.flow.Flow

interface CompanyRepository {
    fun observeCompany(id: String): Flow<Company?>
    suspend fun getCompany(id: String): Company?
    fun observeAllCompanies(): Flow<List<Company>>
    suspend fun saveCompany(company: Company)
    suspend fun updateCompany(company: Company)
    suspend fun deleteCompany(id: String)
}