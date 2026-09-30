package com.example.myapplication.domain.repository

import com.example.myapplication.domain.results.AppResult

interface PdfRepository {
    /** Erzeugt das PDF, speichert es und aktualisiert Invoice.pdfFileReference. Gibt den Dateipfad zurück. */
    suspend fun generateInvoicePdf(invoiceId: String): AppResult<String>

    /** Teilt eine bereits erzeugte PDF-Datei. Erzeugt sie bei Bedarf zuerst neu (z.B. falls gelöscht). */
    suspend fun shareInvoicePdf(invoiceId: String): AppResult<Unit>
}