package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.repository.PdfRepository
import com.example.myapplication.domain.results.AppResult

class ShareInvoicePdfUseCase(
    private val pdfRepository: PdfRepository
) {
    suspend operator fun invoke(invoiceId: String): AppResult<Unit> = pdfRepository.shareInvoicePdf(invoiceId)
}