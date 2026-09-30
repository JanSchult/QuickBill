package com.example.myapplication.domain.pdf

fun String.sanitizeForFileName(): String =
    replace(Regex("[^A-Za-z0-9äöüÄÖÜß_-]"), "_")