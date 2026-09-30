package com.example.myapplication.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(
    tableName = "customers",
    foreignKeys = [
        ForeignKey(
            entity = CompanyEntity::class,
            parentColumns = ["id"],
            childColumns = ["companyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("companyId"),
        Index(value = ["companyId", "customerNumber"], unique = true)
    ]
)
data class CustomerEntity(
    @PrimaryKey val id: String,
    val companyId: String,
    val customerNumber: String,
    val companyName: String,
    val firstName: String,
    val lastName: String,
    val contactPerson: String,
    val street: String,
    val houseNumber: String,
    val postalCode: String,
    val city: String,
    val country: String,
    val email: String,
    val phone: String,
    val vatId: String,
    val defaultPaymentTermDays: Int?,
    val notes: String,
    val isPrivateCustomer: Boolean,
    val isArchived: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val lastUsedAt: LocalDateTime?   // für "nach zuletzt verwendet sortieren"
)