package com.yuri.love.utils

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.number
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object TimeUtils {
    @OptIn(ExperimentalTime::class)
    val now: Long get() = Clock.System.now().toEpochMilliseconds()

    @OptIn(ExperimentalTime::class)
    val nowTime = Clock.System.now()

    @OptIn(ExperimentalTime::class)
    fun formatTimestampDay(timestampMillis: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestampMillis)
        val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date

        return "${localDate.year}年${localDate.month.number}月${localDate.day}日"
    }

    @OptIn(ExperimentalTime::class)
    fun formatTimestampTime(timestampMillis: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestampMillis)
        val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).time

        return "${localDate.hour}时${localDate.minute}分"
    }

    @OptIn(ExperimentalTime::class)
    fun calculateTimeDifference(targetTime: String): String {
        val zone = TimeZone.currentSystemDefault()

        val formatter = LocalDateTime.parse(targetTime.replace(" ", "T"))
        val now = nowTime.toLocalDateTime(zone)

        val startInstant = formatter.toInstant(zone)
        val nowInstant = now.toInstant(zone)
        val duration: Duration = nowInstant - startInstant

        return "${duration.inWholeDays}天"
    }
}