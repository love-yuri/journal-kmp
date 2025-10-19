package com.yuri.love.views.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.yuri.love.components.SimpleTopBar

class SettingScreen: Screen {
    @Composable
    override fun Content() {
        Column(modifier = Modifier.statusBarsPadding()) {
            SimpleTopBar()
        }
    }
}