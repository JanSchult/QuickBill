package com.example.myapplication.util


import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.time.Instant

/**
 * Abstraktion über die aktuelle Zeit, austauschbar über Koin (clockModule, Phase 3/5),
 * damit Use-Case-Tests eine feste Zeit vorgeben können statt echter Systemzeit.
 *
 * Hinweis: Seit kotlinx-datetime 0.7.x leben Instant/Clock in kotlin.time (Stdlib) -
 * daher der Import aus kotlin.time statt kotlinx.datetime.
 */
interface AppClock {
    fun nowInstant(): Instant
    fun today(): LocalDate
    fun nowDateTime(): LocalDateTime
}

