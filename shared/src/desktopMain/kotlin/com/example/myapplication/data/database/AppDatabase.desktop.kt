package com.example.myapplication.data.database

@Suppress(names = ["NO_ACTUAL_FOR_EXPECT"])
actual object AppDatabaseConstructor :
    androidx.room.RoomDatabaseConstructor<com.example.myapplication.data.database.AppDatabase> {
    actual override fun initialize(): com.example.myapplication.data.database.AppDatabase {
        TODO("Not yet implemented")
    }
}