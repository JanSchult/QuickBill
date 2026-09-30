package com.example.myapplication.domain.model

import kotlin.jvm.JvmInline

/**
 * Steuersatz in Basispunkten (1900 = 19,00 %). Basispunkte statt Prozent-Int,
 * um künftige krumme Sätze (z. B. 7,7 % Schweiz) exakt abzubilden - erfüllt
 * die Anforderung "frei definierbarer Steuersatz für spätere Erweiterungen".
 */
@JvmInline
value class TaxRate(val basisPoints: Int) {
    init {
        require(basisPoints in 0..1_000_000) { "Steuersatz außerhalb des zulässigen Bereichs: $basisPoints" }
    }

    fun asPercentageString(): String {
        val whole = basisPoints / 100
        val fraction = basisPoints % 100
        return if (fraction == 0) "$whole %" else "$whole,${fraction.toString().padStart(2, '0')} %"
    }

    companion object {
        val ZERO = TaxRate(0)
        val REDUCED_DE = TaxRate(700)
        val STANDARD_DE = TaxRate(1900)

        fun ofPercent(percent: Int): TaxRate = TaxRate(percent * 100)
    }
}