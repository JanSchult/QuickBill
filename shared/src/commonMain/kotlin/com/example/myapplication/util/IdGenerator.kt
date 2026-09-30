package com.example.myapplication.util


import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Zentrale ID-Erzeugung. String-UUIDs statt Auto-Increment-Longs,
 * um spätere Cloud-Synchronisierung ohne Kollisionsrisiko zu ermöglichen.
 */
object IdGenerator {
    @OptIn(ExperimentalUuidApi::class)
    fun newId(): String = Uuid.random().toString()
}