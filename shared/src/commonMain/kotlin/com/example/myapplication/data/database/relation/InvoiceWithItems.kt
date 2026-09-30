package com.example.myapplication.data.database.relation

import com.example.myapplication.data.database.entity.InvoiceEntity
import androidx.room.Relation
import androidx.room.Embedded
import com.example.myapplication.data.database.entity.InvoiceItemEntity

data class InvoiceWithItems(
    @Embedded val invoice: InvoiceEntity,
    @Relation(parentColumn = "id", entityColumn = "invoiceId")
    val items: List<InvoiceItemEntity>
)