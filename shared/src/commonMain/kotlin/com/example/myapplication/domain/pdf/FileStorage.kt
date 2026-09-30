package com.example.myapplication.domain.pdf

interface FileStorage {
    suspend fun saveInvoicePdf(fileName: String, bytes: ByteArray): String
}