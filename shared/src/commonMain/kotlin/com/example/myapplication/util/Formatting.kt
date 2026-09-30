package com.example.myapplication.util

import com.example.myapplication.domain.model.Customer
import com.example.myapplication.domain.model.Money
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun Money.formatEuro(): String {
    val sign = if (minorUnits < 0) "-" else ""
    val abs = kotlin.math.abs(minorUnits)
    val major = abs / 100
    val minor = (abs % 100).toString().padStart(2, '0')
    return "$sign$major,$minor €"
}

fun LocalDate.formatGerman(): String {
    val dd = dayOfMonth.toString().padStart(2, '0')
    val mm = monthNumber.toString().padStart(2, '0')
    return "$dd.$mm.$year"
}

/** Wandelt Tausendstel (2500 = 2,5) in eine editierbare Anzeige-Zeichenkette um. */
fun Long.thousandthsToDisplayString(): String {
    val whole = this / 1000
    val fraction = this % 1000
    return if (fraction == 0L) "$whole" else "${whole},${fraction.toString().padStart(3, '0').trimEnd('0')}"
}

fun String.parseToThousandths(): Long {
    val normalized = trim().replace(",", ".")
    val parts = normalized.split(".")
    val whole = parts.getOrElse(0) { "0" }.toLongOrNull() ?: 0L
    val fractionRaw = parts.getOrElse(1) { "0" }.padEnd(3, '0').take(3)
    val fraction = fractionRaw.toLongOrNull() ?: 0L
    return whole * 1000 + fraction
}

fun Int.basisPointsToPercentDisplay(): String {
    val whole = this / 100
    val fraction = this % 100
    return if (fraction == 0) "$whole" else "$whole,${fraction.toString().padStart(2, '0')}"
}

fun String.parsePercentToBasisPoints(): Int {
    val normalized = trim().replace(",", ".")
    val value = normalized.toDoubleOrNull() ?: 0.0
    return (value * 100).toInt()
}

fun Customer.displayName(): String =
    companyName.ifBlank { "$firstName $lastName".trim() }

@OptIn(ExperimentalTime::class)
fun LocalDate.toEpochMillisUtc(): Long = atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

@OptIn(ExperimentalTime::class)
fun Long.toLocalDateFromEpochMillisUtc(): LocalDate =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date
