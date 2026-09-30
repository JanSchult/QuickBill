package com.example.myapplication.domain.pdf

data class PdfInvoiceData(
    val companyName: String,
    val companyAddressLines: List<String>,
    val companyEmail: String,
    val companyPhone: String,
    val companyWebsite: String,
    val companyTaxNumber: String,
    val companyVatId: String,
    val companyBankName: String,
    val companyIban: String,
    val companyBic: String,
    val companyLogo: ByteArray?,

    val customerName: String,
    val customerAddressLines: List<String>,

    val invoiceNumber: String,
    val invoiceDateDisplay: String,
    val serviceDateDisplay: String?,
    val dueDateDisplay: String,
    val paymentTermsText: String,

    val items: List<PdfInvoiceItemRow>,
    val taxBreakdown: List<PdfTaxBreakdownRow>,

    val netAmountFormatted: String,
    val taxAmountFormatted: String,
    val grossAmountFormatted: String,

    val isSmallBusiness: Boolean,
    val smallBusinessNoticeText: String,

    val note: String,
    val additionalHint: String,
    val footerLegalText: String
) {
    // ByteArray erzwingt manuelle equals/hashCode, wie bei CompanyEntity (Phase 4a) -
    // hier v.a. relevant, falls dieses Modell einmal in Tests per data-class-Vergleich geprüft wird.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PdfInvoiceData) return false
        return companyName == other.companyName && invoiceNumber == other.invoiceNumber &&
                items == other.items && (companyLogo?.contentEquals(other.companyLogo) ?: (other.companyLogo == null))
    }

    override fun hashCode(): Int {
        var result = companyName.hashCode()
        result = 31 * result + invoiceNumber.hashCode()
        result = 31 * result + (companyLogo?.contentHashCode() ?: 0)
        return result
    }
}