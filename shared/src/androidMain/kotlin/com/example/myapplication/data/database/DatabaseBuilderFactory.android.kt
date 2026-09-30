package com.example.myapplication.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlin.jvm.java

actual class DatabaseBuilderFactory(private val context: Context) {
    actual fun create(): RoomDatabase.Builder<AppDatabase> {
        val dbFile = context.getDatabasePath("rechnungs-app.db")
        return Room.databaseBuilder(context, AppDatabase::class.java, dbFile.absolutePath)
    }
}