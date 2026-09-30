package com.example.myapplication.domain.model

enum class ItemUnit(val dbValue: String, val displayLabel: String) {
    PIECE("PIECE", "Stück"),
    HOUR("HOUR", "Stunde"),
    DAY("DAY", "Tag"),
    MONTH("MONTH", "Monat"),
    FLAT_RATE("FLAT_RATE", "Pauschal"),
    KILOMETER("KILOMETER", "Kilometer"),
    GRAM("GRAM", "Gramm"),
    KILOGRAM("KILOGRAM", "Kilogramm"),
    LITER("LITER", "Liter"),
    METER("METER", "Meter");

    companion object {
        fun fromDbValue(value: String): ItemUnit =
            entries.firstOrNull { it.dbValue == value }
                ?: throw IllegalArgumentException("Unbekannte Einheit: $value")
    }
}