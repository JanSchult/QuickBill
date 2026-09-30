package com.example.myapplication.data.repository

import com.example.myapplication.domain.model.Company
import com.example.myapplication.domain.model.Customer
import com.example.myapplication.domain.model.Invoice
import com.example.myapplication.domain.model.InvoiceItem
import com.example.myapplication.domain.pdf.FileSharer
import com.example.myapplication.domain.pdf.FileStorage
import com.example.myapplication.domain.pdf.InvoiceTaxBreakdownCalculator
import com.example.myapplication.domain.pdf.PdfInvoiceData
import com.example.myapplication.domain.pdf.PdfInvoiceItemRow
import com.example.myapplication.domain.pdf.PdfLayoutConfig
import com.example.myapplication.domain.pdf.PdfRenderer
import com.example.myapplication.domain.pdf.PdfTaxBreakdownRow
import com.example.myapplication.domain.pdf.sanitizeForFileName
import com.example.myapplication.domain.repository.AppSettingsRepository
import com.example.myapplication.domain.repository.CompanyRepository
import com.example.myapplication.domain.repository.CustomerRepository
import com.example.myapplication.domain.repository.InvoiceItemRepository
import com.example.myapplication.domain.repository.InvoiceRepository
import com.example.myapplication.domain.repository.PdfRepository
import com.example.myapplication.domain.results.AppResult
import com.example.myapplication.domain.results.ValidationErrorItem
import com.example.myapplication.util.AppClock
import com.example.myapplication.util.displayName
import com.example.myapplication.util.formatEuro
import com.example.myapplication.util.formatGerman

class PdfRepositoryImpl(
    private val invoiceRepository: InvoiceRepository,
    private val invoiceItemRepository: InvoiceItemRepository,
    private val customerRepository: CustomerRepository,
    private val companyRepository: CompanyRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val pdfRenderer: PdfRenderer,
    private val fileStorage: FileStorage,
    private val fileSharer: FileSharer,
    private val clock: AppClock
) : PdfRepository {

    override suspend fun generateInvoicePdf(invoiceId: String): AppResult<String> {
        val invoice = invoiceRepository.getById(invoiceId)
            ?: return AppResult.Failure(NoSuchElementException("Rechnung nicht gefunden: $invoiceId"))

        val customer = invoice.customerId?.let { customerRepository.getById(it) }
            ?: return AppResult.ValidationError(
                listOf(
                    ValidationErrorItem(
                        "customerId",
                        "PDF kann erst nach Kundenauswahl erzeugt werden."
                    )
                )
            )

        val company = companyRepository.getCompany(invoice.companyId)
            ?: return AppResult.Failure(IllegalStateException("Kein Unternehmensprofil hinterlegt."))

        val items = invoiceItemRepository.getForInvoice(invoiceId)
        val settings = appSettingsRepository.get()

        return try {
            val data = buildPdfData(invoice, items, customer, company, settings?.smallBusinessNoticeText)
            val bytes = pdfRenderer.render(data, PdfLayoutConfig.default())
            val fileName = "${invoice.invoiceNumber.sanitizeForFileName()}.pdf"
            val path = fileStorage.saveInvoicePdf(fileName, bytes)

            invoiceRepository.update(invoice.copy(pdfFileReference = path, updatedAt = clock.nowDateTime()))
            AppResult.Success(path)
        } catch (e: Exception) {
            AppResult.Failure(e)
        }
    }

    override suspend fun shareInvoicePdf(invoiceId: String): AppResult<Unit> {
        val invoice = invoiceRepository.getById(invoiceId)
            ?: return AppResult.Failure(NoSuchElementException("Rechnung nicht gefunden: $invoiceId"))

        val existingPath = invoice.pdfFileReference
        val pathResult = if (existingPath != null) {
            AppResult.Success(existingPath)
        } else {
            generateInvoicePdf(invoiceId)
        }

        return when (pathResult) {
            is AppResult.Success -> try {
                fileSharer.shareFile(pathResult.data)
                AppResult.Success(Unit)
            } catch (e: Exception) {
                AppResult.Failure(e)
            }
            is AppResult.Failure -> pathResult
            is AppResult.ValidationError -> pathResult
        }
    }

    private fun buildPdfData(
        invoice: Invoice,
        items: List<InvoiceItem>,
        customer: Customer,
        company: Company,
        smallBusinessNoticeText: String?
    ): PdfInvoiceData {
        val taxBreakdown = InvoiceTaxBreakdownCalculator.calculate(items)

        return PdfInvoiceData(
            companyName = company.name,
            companyAddressLines = listOf(
                "${company.street} ${company.houseNumber}",
                "${company.postalCode} ${company.city}",
                company.country
            ),
            companyEmail = company.email,
            companyPhone = company.phone,
            companyWebsite = company.website,
            companyTaxNumber = company.taxNumber,
            companyVatId = company.vatId,
            companyBankName = company.bankName,
            companyIban = company.iban,
            companyBic = company.bic,
            companyLogo = company.logo,

            customerName = customer.displayName(),
            customerAddressLines = listOf(
                "${customer.street} ${customer.houseNumber}",
                "${customer.postalCode} ${customer.city}",
                customer.country
            ),

            invoiceNumber = invoice.invoiceNumber,
            invoiceDateDisplay = invoice.invoiceDate.formatGerman(),
            serviceDateDisplay = invoice.serviceDate?.formatGerman(),
            dueDateDisplay = invoice.dueDate.formatGerman(),
            paymentTermsText = invoice.paymentTerms,

            items = items.sortedBy { it.position }.map { item ->
                PdfInvoiceItemRow(
                    position = item.position,
                    description = item.description,
                    quantityAndUnitDisplay = "${item.quantityThousandths / 1000.0} ${item.unit.displayLabel}",
                    unitPriceFormatted = item.unitPriceNet.formatEuro(),
                    taxRateDisplay = item.taxRate.asPercentageString(),
                    netAmountFormatted = item.netAmount.formatEuro()
                )
            },
            taxBreakdown = taxBreakdown.map {
                PdfTaxBreakdownRow(
                    taxRateDisplay = it.taxRate.asPercentageString(),
                    netAmountFormatted = it.netAmount.formatEuro(),
                    taxAmountFormatted = it.taxAmount.formatEuro()
                )
            },

            netAmountFormatted = invoice.netAmount.formatEuro(),
            taxAmountFormatted = invoice.taxAmount.formatEuro(),
            grossAmountFormatted = invoice.grossAmount.formatEuro(),

            isSmallBusiness = company.isSmallBusiness,
            smallBusinessNoticeText = smallBusinessNoticeText ?: "Gemäß § 19 UStG wird keine Umsatzsteuer berechnet.",

            note = invoice.note,
            additionalHint = invoice.additionalHint,
            footerLegalText = "Diese Rechnung wurde elektronisch erstellt und ist ohne Unterschrift gültig."
        )
    }
}