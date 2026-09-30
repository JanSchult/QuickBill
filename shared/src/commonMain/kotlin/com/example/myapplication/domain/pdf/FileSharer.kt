package com.example.myapplication.domain.pdf

interface FileSharer {
    suspend fun shareFile(pathOrUri: String, mimeType: String = "application/pdf")
}
