package com.example.myapplication.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.myapplication.domain.model.ItemUnit

@Entity(
    tableName = "invoice_items",
    foreignKeys = [
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("invoiceId")]
)
data class InvoiceItemEntity(
    @PrimaryKey val id: String,
    val invoiceId: String,
    val position: Int,
    val description: String,
    val quantityThousandths: Long,   // Menge * 1000 (z. B. 2,5 Stunden = 2500) - vermeidet Float
    val unit: ItemUnit,
    val unitPriceNetMinorUnits: Long,
    val discountBasisPoints: Int,
    val taxRateBasisPoints: Int,
    val netAmountMinorUnits: Long,
    val taxAmountMinorUnits: Long,
    val grossAmountMinorUnits: Long
)