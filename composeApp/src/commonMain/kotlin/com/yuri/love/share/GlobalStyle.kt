package com.yuri.love.share

import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontFamily
import journal.composeapp.generated.resources.MapleMono_NF_CN_Medium
import journal.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

/**
 * 全局样式
 */
object GlobalStyle {
    // 默认涟漪效果
    val ripple = ripple(
        bounded = false,
        color = Color(0xFFFFB3C6).copy(alpha = 0.2f)
    )

    // 渐变背景色
    val softPinkGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFDF2F8), // 粉嫩白
            Color(0xFFFCE7F3)  // 樱花粉白
        )
    )

    // 顶栏背景色
    val tapBarBackground = Color(0xFFFDF2F8).copy(alpha = 0.8f)

    // 字体
    val MapleMonoFont: FontFamily
        @Composable
        get() = if (LocalInspectionMode.current) {
            FontFamily.Default
        } else {
            FontFamily(Font(Res.font.MapleMono_NF_CN_Medium))
        }
}