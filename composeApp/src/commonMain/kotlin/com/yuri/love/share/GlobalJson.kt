package com.yuri.love.share

import kotlinx.serialization.json.Json

/**
 * 全局json
 */
val json = Json {
    ignoreUnknownKeys = true // 忽略JSON中的未知键
    isLenient = true // 宽松模式
    coerceInputValues = true // 强制输入值
}
