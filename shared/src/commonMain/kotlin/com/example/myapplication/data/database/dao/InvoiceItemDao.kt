package com.example.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.database.entity.InvoiceItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceItemDao {

    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invoiceId ORDER BY position ASC")
    fun observeForInvoice(invoiceId: String): Flow<List<InvoiceItemEntity>>

    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invoiceId ORDER BY position ASC")
    suspend fun getForInvoice(invoiceId: String): List<InvoiceItemEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(item: InvoiceItemEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(items: List<InvoiceItemEntity>)

    @Update
    suspend fun update(item: InvoiceItemEntity)

    @Query("DELETE FROM invoice_items WHERE id = :itemId")
    suspend fun delete(itemId: String)

    @Query("DELETE FROM invoice_items WHERE invoiceId = :invoiceId")
    suspend fun deleteAllForInvoice(invoiceId: String)
}