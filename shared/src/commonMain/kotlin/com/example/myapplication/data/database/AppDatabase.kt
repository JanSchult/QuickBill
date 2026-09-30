package com.example.myapplication.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.example.myapplication.data.database.dao.AppSettingsDao
import com.example.myapplication.data.database.dao.CompanyDao
import com.example.myapplication.data.database.dao.CustomerDao
import com.example.myapplication.data.database.dao.InvoiceDao
import com.example.myapplication.data.database.dao.InvoiceItemDao
import com.example.myapplication.data.database.dao.InvoiceNumberSequenceDao
import com.example.myapplication.data.database.entity.CompanyEntity
import com.example.myapplication.data.database.entity.CustomerEntity
import com.example.myapplication.data.database.entity.InvoiceItemEntity
import com.example.myapplication.data.database.entity.AppSettingsEntity
import com.example.myapplication.data.database.entity.InvoiceNumberSequenceEntity
import com.example.myapplication.data.database.entity.InvoiceEntity
@Database(
    entities = [
        CompanyEntity::class,
        CustomerEntity::class,
        InvoiceEntity::class,
        InvoiceItemEntity::class,
        InvoiceNumberSequenceEntity::class,
        AppSettingsEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun companyDao(): CompanyDao
    abstract fun customerDao(): CustomerDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun invoiceItemDao(): InvoiceItemDao
    abstract fun invoiceNumberSequenceDao(): InvoiceNumberSequenceDao
    abstract fun appSettingsDao(): AppSettingsDao
}

// Room generiert die `actual`-Implementierung für jeden Target automatisch,
// sobald der KSP-Compiler-Lauf für den jeweiligen Source-Set durchgeführt wurde.
// Die IDE zeigt bis zum ersten erfolgreichen Build fälschlich "keine actual-Deklaration" an -
// das ist unschädlich, siehe offizielle Room-KMP-Doku.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}