package com.example.myapplication.data.repository


import com.example.myapplication.data.database.dao.InvoiceNumberSequenceDao
import com.example.myapplication.data.mapper.toDomain
import com.example.myapplication.data.mapper.toEntity
import com.example.myapplication.domain.model.InvoiceNumberSequence
import com.example.myapplication.domain.repository.InvoiceNumberSequenceRepository

class InvoiceNumberSequenceRepositoryImpl(
    private val dao: InvoiceNumberSequenceDao
) : InvoiceNumberSequenceRepository {

    override suspend fun findSequence(
        companyId: String,
        prefix: String,
        year: Int
    ): InvoiceNumberSequence? {
        return dao.findSequence(companyId, prefix, year)?.toDomain()
    }

    override suspend fun getNextNumber(
        companyId: String,
        prefix: String,
        year: Int
    ): Int {
        return dao.getNextNumber(companyId, prefix, year)
    }

    override suspend fun insertSequence(sequence: InvoiceNumberSequence) {
        dao.insertSequence(sequence.toEntity())
    }

    override suspend fun updateSequence(sequence: InvoiceNumberSequence) {
        dao.updateSequence(sequence.toEntity())
    }
}