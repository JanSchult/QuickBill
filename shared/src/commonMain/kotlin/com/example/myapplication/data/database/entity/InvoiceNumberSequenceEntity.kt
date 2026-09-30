package com.example.myapplication.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "invoice_number_sequences",
    indices = [Index(value = ["companyId", "prefix", "year"], unique = true)]
)
data class InvoiceNumberSequenceEntity(
    @PrimaryKey val id: String,
    val companyId: String,
    val prefix: String,
    val year: Int,
    val lastNumber: Int
)