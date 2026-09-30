package com.example.myapplication.data.database

import androidx.room.TypeConverter
import com.example.myapplication.domain.model.InvoiceStatus
import com.example.myapplication.domain.model.ItemUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class Converters {

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? = value?.toString()

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? = value?.let(LocalDateTime::parse)

    @TypeConverter
    fun fromInvoiceStatus(value: InvoiceStatus?): String? = value?.dbValue

    @TypeConverter
    fun toInvoiceStatus(value: String?): InvoiceStatus? = value?.let(InvoiceStatus::fromDbValue)

    @TypeConverter
    fun fromItemUnit(value: ItemUnit?): String? = value?.dbValue

    @TypeConverter
    fun toItemUnit(value: String?): ItemUnit? = value?.let(ItemUnit::fromDbValue)
}