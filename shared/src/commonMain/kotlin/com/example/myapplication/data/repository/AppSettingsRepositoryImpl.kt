package com.example.myapplication.data.repository


import com.example.myapplication.data.database.dao.AppSettingsDao
import com.example.myapplication.data.mapper.toDomain
import com.example.myapplication.data.mapper.toEntity
import com.example.myapplication.domain.model.AppSettings
import com.example.myapplication.domain.repository.AppSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppSettingsRepositoryImpl(
    private val appSettingsDao: AppSettingsDao
) : AppSettingsRepository {

    override fun observe(): Flow<AppSettings?> {
        return appSettingsDao.observe().map { it?.toDomain() }
    }

    override suspend fun get(): AppSettings? {
        return appSettingsDao.get()?.toDomain()
    }

    override suspend fun save(settings: AppSettings) {
        appSettingsDao.upsert(settings.toEntity())
    }
}