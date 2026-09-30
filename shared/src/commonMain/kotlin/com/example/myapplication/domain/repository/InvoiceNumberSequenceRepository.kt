package com.example.myapplication.domain.repository


import com.example.myapplication.domain.model.InvoiceNumberSequence

interface InvoiceNumberSequenceRepository {
    suspend fun findSequence(companyId: String, prefix: String, year: Int): InvoiceNumberSequence?
    suspend fun getNextNumber(companyId: String, prefix: String, year: Int): Int
    suspend fun insertSequence(sequence: InvoiceNumberSequence)
    suspend fun updateSequence(sequence: InvoiceNumberSequence)
}