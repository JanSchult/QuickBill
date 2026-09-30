package com.example.myapplication.data.mapper


import com.example.myapplication.data.database.entity.AppSettingsEntity
import com.example.myapplication.domain.model.AppSettings

fun AppSettingsEntity.toDomain(): AppSettings = AppSettings(
    activeCompanyId = activeCompanyId,
    invoiceNumberPrefix = invoiceNumberPrefix,
    defaultPaymentTermDays = defaultPaymentTermDays,
    defaultInvoiceNote = defaultInvoiceNote,
    smallBusinessNoticeText = smallBusinessNoticeText,
    pdfLayoutReference = pdfLayoutReference
)

fun AppSettings.toEntity(): AppSettingsEntity = AppSettingsEntity(
    activeCompanyId = activeCompanyId,
    invoiceNumberPrefix = invoiceNumberPrefix,
    defaultPaymentTermDays = defaultPaymentTermDays,
    defaultInvoiceNote = defaultInvoiceNote,
    smallBusinessNoticeText = smallBusinessNoticeText,
    pdfLayoutReference = pdfLayoutReference
)