package com.yuri.love.views.home

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.yuri.love.views.create.components.InputText

class TestScreen: Screen {
    @Composable
    override fun Content() {
        InputText()
    }
}