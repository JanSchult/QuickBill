package com.example.myapplication.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.myapplication.domain.model.InvoiceStatus
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@Entity(
    tableName = "invoices",
    foreignKeys = [
        ForeignKey(
            entity = CompanyEntity::class,
            parentColumns = ["id"],
            childColumns = ["companyId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            // RESTRICT statt CASCADE: ein Kunde mit vorhandenen Rechnungen darf
            // nicht gelöscht werden (Nachvollziehbarkeit) - siehe DeleteCustomerUseCase,
            // das stattdessen zur Archivierung raten soll.
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("companyId"),
        Index("customerId"),
        Index("status"),
        Index(value = ["companyId", "invoiceNumber"], unique = true)
    ]
)
data class InvoiceEntity(
    @PrimaryKey val id: String,
    val companyId: String,
    val customerId: String?,
    val invoiceNumber: String,
    val invoiceDate: LocalDate,
    val serviceDate: LocalDate?,
    val servicePeriodFrom: LocalDate?,
    val servicePeriodTo: LocalDate?,
    val dueDate: LocalDate,
    val status: InvoiceStatus,
    val currency: String,
    val note: String,
    val paymentTerms: String,
    val additionalHint: String,
    val netAmountMinorUnits: Long,
    val taxAmountMinorUnits: Long,
    val grossAmountMinorUnits: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val paidAt: LocalDateTime?,
    val cancelledAt: LocalDateTime?,
    val pdfFileReference: String?
)