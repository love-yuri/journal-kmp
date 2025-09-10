package com.yuri.love.share

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object GlobalValue {
    var weather by mutableStateOf("loading...")
}