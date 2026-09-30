package com.example.myapplication.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: String = SINGLETON_ID,
    val activeCompanyId: String?,
    val invoiceNumberPrefix: String,
    val defaultPaymentTermDays: Int,
    val defaultInvoiceNote: String,
    val smallBusinessNoticeText: String,
    val pdfLayoutReference: String?
) {
    companion object {
        const val SINGLETON_ID = "app_settings_singleton"
    }
}