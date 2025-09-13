package com.yuri.love.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
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
}