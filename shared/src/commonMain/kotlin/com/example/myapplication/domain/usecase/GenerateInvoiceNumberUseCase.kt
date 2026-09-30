package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.repository.InvoiceNumberSequenceRepository

/**
 * Erzeugt eine formatierte, eindeutige Rechnungsnummer: "<Präfix>-<Jahr>-<laufende Nummer>",
 * z. B. "RE-2026-0007". Präfix ist pro Unternehmen frei konfigurierbar (Antwort auf Frage 3).
 */
class GenerateInvoiceNumberUseCase(
    private val invoiceNumberSequenceRepository: InvoiceNumberSequenceRepository
) {
    suspend operator fun invoke(companyId: String, prefix: String, year: Int): String {
        val number = invoiceNumberSequenceRepository.getNextNumber(companyId, prefix, year)
        return "$prefix-$year-${number.toString().padStart(4, '0')}"
    }
}