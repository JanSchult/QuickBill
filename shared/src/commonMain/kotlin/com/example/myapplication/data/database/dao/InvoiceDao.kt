package com.example.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.myapplication.data.database.entity.InvoiceEntity
import com.example.myapplication.data.database.relation.InvoiceWithItems
import com.example.myapplication.domain.model.InvoiceStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface InvoiceDao {

    @Query("SELECT * FROM invoices WHERE companyId = :companyId ORDER BY invoiceDate DESC")
    fun observeAll(companyId: String): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE companyId = :companyId AND status = :status ORDER BY invoiceDate DESC")
    fun observeByStatus(companyId: String, status: InvoiceStatus): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE customerId = :customerId ORDER BY invoiceDate DESC")
    fun observeByCustomer(customerId: String): Flow<List<InvoiceEntity>>

    @Query(
        """
        SELECT * FROM invoices
        WHERE companyId = :companyId AND status IN ('OPEN', 'SENT', 'PARTIALLY_PAID')
        ORDER BY dueDate ASC
        """
    )
    fun observeOpenInvoices(companyId: String): Flow<List<InvoiceEntity>>

    @Query(
        """
        SELECT * FROM invoices
        WHERE companyId = :companyId
        AND status IN ('OPEN', 'SENT', 'PARTIALLY_PAID')
        AND dueDate < :today
        ORDER BY dueDate ASC
        """
    )
    fun observeOverdueInvoices(companyId: String, today: LocalDate): Flow<List<InvoiceEntity>>

    @Query(
        """
        SELECT invoices.* FROM invoices
        LEFT JOIN customers ON customers.id = invoices.customerId
        WHERE invoices.companyId = :companyId
        AND (
            invoices.invoiceNumber LIKE '%' || :query || '%'
            OR customers.companyName LIKE '%' || :query || '%'
            OR customers.lastName LIKE '%' || :query || '%'
        )
        ORDER BY invoices.invoiceDate DESC
        """
    )
    fun search(companyId: String, query: String): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    fun observeById(invoiceId: String): Flow<InvoiceEntity?>

    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    suspend fun getById(invoiceId: String): InvoiceEntity?

    @Transaction
    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    fun observeWithItems(invoiceId: String): Flow<InvoiceWithItems?>

    @Query("SELECT EXISTS(SELECT 1 FROM invoices WHERE companyId = :companyId AND invoiceNumber = :invoiceNumber)")
    suspend fun existsByNumber(companyId: String, invoiceNumber: String): Boolean

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(invoice: InvoiceEntity)

    @Update
    suspend fun update(invoice: InvoiceEntity)

    // Nur Entwürfe dürfen hart gelöscht werden - alles andere wird storniert (CancelInvoiceUseCase),
    // damit die Nachvollziehbarkeit erhalten bleibt (Phase 7-Vorgabe).
    @Query("DELETE FROM invoices WHERE id = :invoiceId AND status = 'DRAFT'")
    suspend fun deleteDraft(invoiceId: String): Int
}