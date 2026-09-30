package com.example.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.database.entity.CompanyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyDao {

    @Query("SELECT * FROM companies WHERE id = :companyId")
    fun observeById(companyId: String): Flow<CompanyEntity?>

    @Query("SELECT * FROM companies WHERE id = :companyId")
    suspend fun getById(companyId: String): CompanyEntity?

    @Query("SELECT * FROM companies ORDER BY name ASC")
    fun observeAll(): Flow<List<CompanyEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(company: CompanyEntity)

    @Update
    suspend fun update(company: CompanyEntity)

    @Query("DELETE FROM companies WHERE id = :companyId")
    suspend fun delete(companyId: String)
}