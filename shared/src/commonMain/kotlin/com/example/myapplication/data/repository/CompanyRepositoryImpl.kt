package com.example.myapplication.data.repository

import com.example.myapplication.data.database.dao.CompanyDao
import com.example.myapplication.data.mapper.toDomain
import com.example.myapplication.data.mapper.toEntity
import com.example.myapplication.domain.model.Company
import com.example.myapplication.domain.repository.CompanyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map



class CompanyRepositoryImpl(
    private val companyDao: CompanyDao
) : CompanyRepository {

    override fun observeCompany(id: String): Flow<Company?> {
        return companyDao.observeById(id).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun getCompany(id: String): Company? {
        return companyDao.getById(id)?.toDomain()
    }

    override fun observeAllCompanies(): Flow<List<Company>> {
        return companyDao.observeAll().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun saveCompany(company: Company) {
        companyDao.insert(company.toEntity())
    }

    override suspend fun updateCompany(company: Company) {
        companyDao.update(company.toEntity())
    }

    override suspend fun deleteCompany(id: String) {
        companyDao.delete(id)
    }
}