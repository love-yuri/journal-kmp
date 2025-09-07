package com.yuri.love.utils

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode

@Composable
fun Modifier.platformSafeTopPadding(): Modifier {
    if (!LocalInspectionMode.current) {
        return this.statusBarsPadding()
    }
    return this
}