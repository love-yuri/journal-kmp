package com.yuri.love.styles

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// 全局颜色
object GlobalColors {

    // 渐变背景色
    val softPinkGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFCE7F3), // 浅粉色
            Color(0xFFFEFBFF), // 极淡的粉白色
            Color(0xFFFFFFFF), // 纯白色
            Color(0xFFFFF0F3)  // 极淡的玫瑰白
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    // 顶栏背景色
    val tapBarBackground = Color(0xFFFDF2F8).copy(alpha = 0.8f)

}