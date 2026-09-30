package com.example.myapplication.data.database

import androidx.room.RoomDatabase

expect class DatabaseBuilderFactory {
    fun create(): RoomDatabase.Builder<AppDatabase>
}