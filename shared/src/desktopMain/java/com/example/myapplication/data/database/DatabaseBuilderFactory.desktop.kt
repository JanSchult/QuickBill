package com.example.myapplication.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual class DatabaseBuilderFactory {
    actual fun create(): RoomDatabase.Builder<AppDatabase> {
        val appDataDir = File(System.getProperty("user.home"), ".rechnungs-app")
        appDataDir.mkdirs()
        val dbFile = File(appDataDir, "rechnungs-app.db")
        return Room.databaseBuilder<AppDatabase>(
            name = dbFile.absolutePath,
            factory = { AppDatabaseConstructor.initialize() }   // ## ergänzt: JVM/Desktop braucht ihn ebenfalls für Konsistenz
        )
    }
}