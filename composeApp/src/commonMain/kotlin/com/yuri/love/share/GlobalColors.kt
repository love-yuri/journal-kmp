package com.yuri.love.share

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// 全局颜色
object GlobalColors {

    // 渐变背景色
    val softPinkGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFDF2F8), // 粉嫩白
            Color(0xFFFCE7F3)  // 樱花粉白
        )
    )

    // 顶栏背景色
    val tapBarBackground = Color(0xFFFDF2F8).copy(alpha = 0.8f)

}