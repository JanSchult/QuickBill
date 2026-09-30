package com.example.myapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.data.database.entity.AppSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {

    @Query("SELECT * FROM app_settings WHERE id = :id")
    fun observe(id: String = AppSettingsEntity.SINGLETON_ID): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = :id")
    suspend fun get(id: String = AppSettingsEntity.SINGLETON_ID): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: AppSettingsEntity)
}