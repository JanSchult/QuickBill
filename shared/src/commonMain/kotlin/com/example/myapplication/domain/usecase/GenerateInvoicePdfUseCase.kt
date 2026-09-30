package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.repository.PdfRepository
import com.example.myapplication.domain.results.AppResult

class GenerateInvoicePdfUseCase(
    private val pdfRepository: PdfRepository
) {
    suspend operator fun invoke(invoiceId: String): AppResult<String> = pdfRepository.generateInvoicePdf(invoiceId)
}