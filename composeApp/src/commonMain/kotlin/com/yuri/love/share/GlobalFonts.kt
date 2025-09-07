package com.yuri.love.share

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontFamily
import journal.composeapp.generated.resources.MapleMono_NF_CN_Medium
import journal.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

// 全局字体
object GlobalFonts {

    val MapleMonoFont: FontFamily
        @Composable
        get() = if (LocalInspectionMode.current) {
            FontFamily.Default
        } else {
            FontFamily(Font(Res.font.MapleMono_NF_CN_Medium))
        }
}