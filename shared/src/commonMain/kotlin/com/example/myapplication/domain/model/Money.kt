package com.example.myapplication.domain.model

import kotlin.jvm.JvmInline


/**
 * Geldbetrag in kleinster Währungseinheit (Cent) als Long.
 * Vermeidet Float/Double-Rundungsfehler bei allen Rechnungsberechnungen.
 */
@JvmInline
value class Money(val minorUnits: Long) : Comparable<Money> {

    operator fun plus(other: Money): Money = Money(minorUnits + other.minorUnits)
    operator fun minus(other: Money): Money = Money(minorUnits - other.minorUnits)
    operator fun times(factor: Long): Money = Money(minorUnits * factor)
    operator fun unaryMinus(): Money = Money(-minorUnits)

    override fun compareTo(other: Money): Int = minorUnits.compareTo(other.minorUnits)

    fun isNegative(): Boolean = minorUnits < 0
    fun isZero(): Boolean = minorUnits == 0L

    companion object {
        val ZERO = Money(0)

        /**
         * Parst einen Dezimalstring aus der UI (z. B. "75,00" oder "75.00") in Money.
         * Kaufmännische Behandlung: fehlende Nachkommastellen werden mit 0 aufgefüllt,
         * überzählige abgeschnitten (keine automatische Rundung an dieser Stelle,
         * das UI soll bereits maximal 2 Nachkommastellen zulassen).
         */
        fun fromDecimalString(value: String): Money {
            val normalized = value.trim().replace(",", ".")
            val isNegative = normalized.startsWith("-")
            val unsigned = normalized.removePrefix("-")
            val parts = unsigned.split(".")
            val major = parts.getOrElse(0) { "0" }.toLongOrNull() ?: 0L
            val minorRaw = parts.getOrElse(1) { "0" }.padEnd(2, '0').take(2)
            val minor = minorRaw.toLongOrNull() ?: 0L
            val total = major * 100 + minor
            return Money(if (isNegative) -total else total)
        }
    }
}

/**
 * Wendet einen Steuersatz (in Basispunkten, siehe [de.janschult.rechnungsapp.domain.model.TaxRate])
 * kaufmännisch gerundet (Half-Up) auf einen Geldbetrag an.
 */
fun Money.applyBasisPoints(basisPoints: Int): Money {
    val numerator = minorUnits * basisPoints
    val rounded = if (numerator >= 0) {
        (numerator + 5_000) / 10_000
    } else {
        (numerator - 5_000) / 10_000
    }
    return Money(rounded)
}