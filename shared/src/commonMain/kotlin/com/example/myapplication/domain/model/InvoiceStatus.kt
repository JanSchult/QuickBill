package com.example.myapplication.domain.model

/**
 * Rechnungsstatus. Wird in Room über den stabilen dbValue-String gespeichert,
 * NICHT über ordinal() - die Enum-Reihenfolge darf sich künftig ändern,
 * ohne bestehende Datenbanken zu brechen (siehe Phase 8).
 */
enum class InvoiceStatus(val dbValue: String) {
    DRAFT("DRAFT"),
    OPEN("OPEN"),
    SENT("SENT"),
    PARTIALLY_PAID("PARTIALLY_PAID"),
    PAID("PAID"),
    OVERDUE("OVERDUE"),
    CANCELLED("CANCELLED");

    companion object {
        fun fromDbValue(value: String): InvoiceStatus =
            entries.firstOrNull { it.dbValue == value }
                ?: throw IllegalArgumentException("Unbekannter Rechnungsstatus: $value")
    }
}