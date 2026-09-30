package com.example.myapplication.domain.repository


import com.example.myapplication.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    fun observe(): Flow<AppSettings?>
    suspend fun get(): AppSettings?
    suspend fun save(settings: AppSettings)
}