package com.yuri.love.share

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


object GlobalValue {
    // 当前天气
    var weather by mutableStateOf("loading...")

    // 导航管理
    val navigatorManager = NavigatorManager
}