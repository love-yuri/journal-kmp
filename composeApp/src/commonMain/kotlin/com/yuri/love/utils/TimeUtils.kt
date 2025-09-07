package com.yuri.love.utils

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

object TimeUtils {
    @OptIn(ExperimentalTime::class)
    val now: Long get() = Clock.System.now().toEpochMilliseconds()
}