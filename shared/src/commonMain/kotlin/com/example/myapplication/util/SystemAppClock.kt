package com.example.myapplication.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class SystemAppClock(
    private val timeZone: TimeZone = TimeZone.currentSystemDefault()
) : AppClock {
    override fun nowInstant(): Instant = Clock.System.now()
    override fun today(): LocalDate = nowInstant().toLocalDateTime(timeZone).date
    override fun nowDateTime(): LocalDateTime = nowInstant().toLocalDateTime(timeZone)
}