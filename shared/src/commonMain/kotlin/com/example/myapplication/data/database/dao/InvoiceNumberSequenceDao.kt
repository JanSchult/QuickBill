package com.example.myapplication.data.database.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.myapplication.data.database.entity.InvoiceNumberSequenceEntity
import com.example.myapplication.util.IdGenerator

@Dao
interface InvoiceNumberSequenceDao {

    @Query("SELECT * FROM invoice_number_sequences WHERE companyId = :companyId AND prefix = :prefix AND year = :year")
    suspend fun findSequence(companyId: String, prefix: String, year: Int): InvoiceNumberSequenceEntity?

    @Insert
    suspend fun insertSequence(sequence: InvoiceNumberSequenceEntity)

    @Update
    suspend fun updateSequence(sequence: InvoiceNumberSequenceEntity)

    /**
     * Vergibt atomar die nächste laufende Nummer für (companyId, prefix, year).
     * @Transaction bündelt Lesen+Schreiben in einer DB-Transaktion; Room serialisiert
     * Schreibzugriffe über eine einzelne Writer-Connection, wodurch zwei gleichzeitige
     * Finalisierungen nicht dieselbe Nummer erhalten können (Phase 7: Race-Condition-Schutz).
     */
    @Transaction
    suspend fun getNextNumber(companyId: String, prefix: String, year: Int): Int {
        val existing = findSequence(companyId, prefix, year)
        return if (existing == null) {
            insertSequence(
                InvoiceNumberSequenceEntity(
                    id = IdGenerator.newId(),
                    companyId = companyId,
                    prefix = prefix,
                    year = year,
                    lastNumber = 1
                )
            )
            1
        } else {
            val next = existing.lastNumber + 1
            updateSequence(existing.copy(lastNumber = next))
            next
        }
    }
}