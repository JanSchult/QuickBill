package com.example.myapplication.domain.pdf

data class PdfLayoutConfig(
    val pageWidthPoints: Float = 595f,   // A4 bei 72 dpi
    val pageHeightPoints: Float = 842f,
    val marginPoints: Float = 40f,
    val primaryColorHex: String = "#1565C0",
    val fontFamily: String = "Helvetica",
    val baseFontSizePoints: Float = 10f,
    val headingFontSizePoints: Float = 16f,
    val showPageNumbers: Boolean = true
) {
    companion object {
        fun default() = PdfLayoutConfig()
    }
}