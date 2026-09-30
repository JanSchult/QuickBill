package com.example.myapplication.domain.pdf

interface PdfRenderer {
    suspend fun render(data: PdfInvoiceData, layout: PdfLayoutConfig): ByteArray
}