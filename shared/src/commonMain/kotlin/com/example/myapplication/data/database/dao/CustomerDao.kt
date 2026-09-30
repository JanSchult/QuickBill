package com.example.myapplication.data.database.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.database.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

@Dao
interface CustomerDao {

    @Query("SELECT * FROM customers WHERE companyId = :companyId AND isArchived = 0 ORDER BY companyName ASC, lastName ASC")
    fun observeActiveSortedByName(companyId: String): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE companyId = :companyId AND isArchived = 0 ORDER BY lastUsedAt DESC")
    fun observeActiveSortedByLastUsed(companyId: String): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE companyId = :companyId AND isArchived = 1 ORDER BY companyName ASC, lastName ASC")
    fun observeArchived(companyId: String): Flow<List<CustomerEntity>>

    @Query(
        """
        SELECT * FROM customers
        WHERE companyId = :companyId
        AND (companyName LIKE '%' || :query || '%'
             OR firstName LIKE '%' || :query || '%'
             OR lastName LIKE '%' || :query || '%'
             OR customerNumber LIKE '%' || :query || '%')
        ORDER BY companyName ASC, lastName ASC
        """
    )
    fun search(companyId: String, query: String): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE id = :customerId")
    fun observeById(customerId: String): Flow<CustomerEntity?>

    @Query("SELECT * FROM customers WHERE id = :customerId")
    suspend fun getById(customerId: String): CustomerEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(customer: CustomerEntity)

    @Update
    suspend fun update(customer: CustomerEntity)

    @Query("UPDATE customers SET isArchived = 1, updatedAt = :updatedAt WHERE id = :customerId")
    suspend fun archive(customerId: String, updatedAt: LocalDateTime)

    @Query("UPDATE customers SET isArchived = 0, updatedAt = :updatedAt WHERE id = :customerId")
    suspend fun restore(customerId: String, updatedAt: LocalDateTime)

    @Query("UPDATE customers SET lastUsedAt = :usedAt WHERE id = :customerId")
    suspend fun touchLastUsed(customerId: String, usedAt: LocalDateTime)

    // Für DeleteCustomerUseCase: nur löschen, wenn keine Rechnungen existieren
    // (sonst RESTRICT-Fremdschlüsselverletzung) - stattdessen archivieren.
    @Query("SELECT EXISTS(SELECT 1 FROM invoices WHERE customerId = :customerId LIMIT 1)")
    suspend fun hasInvoices(customerId: String): Boolean

    @Query("DELETE FROM customers WHERE id = :customerId")
    suspend fun delete(customerId: String)
}