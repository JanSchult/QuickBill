package com.example.myapplication.domain.validation

/** ISO-7064-Mod-97-Prüfsummen-Check, ziffernweise berechnet (kein BigInteger nötig, KMP-sicher). */
object IbanValidator {
    fun isValid(rawIban: String): Boolean {
        val iban = rawIban.replace(" ", "").uppercase()
        if (iban.length !in 15..34) return false
        if (!iban.take(2).all { it.isLetter() }) return false
        if (!iban.drop(2).take(2).all { it.isDigit() }) return false

        val rearranged = iban.substring(4) + iban.substring(0, 4)
        val numeric = rearranged.map { ch -> if (ch.isDigit()) ch.toString() else (ch - 'A' + 10).toString() }
            .joinToString("")

        var remainder = 0
        for (ch in numeric) {
            remainder = (remainder * 10 + (ch - '0')) % 97
        }
        return remainder == 1
    }
}