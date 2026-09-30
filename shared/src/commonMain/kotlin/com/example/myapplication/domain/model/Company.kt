package com.example.myapplication.domain.model

import kotlinx.datetime.LocalDateTime

data class Company(
    val id: String,
    val name: String,
    val street: String,
    val houseNumber: String,
    val postalCode: String,
    val city: String,
    val country: String,
    val email: String,
    val phone: String,
    val website: String,
    val taxNumber: String,
    val vatId: String,
    val bankName: String,
    val iban: String,
    val bic: String,
    val isSmallBusiness: Boolean,
    val defaultCurrency: String,
    val defaultPaymentTermDays: Int,
    val defaultInvoiceNote: String,
    val logo: ByteArray?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Company) return false
        return id == other.id && name == other.name && street == other.street &&
                houseNumber == other.houseNumber && postalCode == other.postalCode &&
                city == other.city && country == other.country && email == other.email &&
                phone == other.phone && website == other.website && taxNumber == other.taxNumber &&
                vatId == other.vatId && bankName == other.bankName && iban == other.iban &&
                bic == other.bic && isSmallBusiness == other.isSmallBusiness &&
                defaultCurrency == other.defaultCurrency &&
                defaultPaymentTermDays == other.defaultPaymentTermDays &&
                defaultInvoiceNote == other.defaultInvoiceNote &&
                (logo?.contentEquals(other.logo) ?: (other.logo == null)) &&
                createdAt == other.createdAt && updatedAt == other.updatedAt
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (logo?.contentHashCode() ?: 0)
        result = 31 * result + createdAt.hashCode()
        return result
    }
}