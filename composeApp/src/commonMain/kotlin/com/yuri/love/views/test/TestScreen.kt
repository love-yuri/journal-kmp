package com.yuri.love.views.test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.yuri.love.views.home.components.LocalDrawerController
import com.yuri.love.components.TopBar

class TestScreen: Screen {
    @Composable
    override fun Content() {
        val drawerController = LocalDrawerController.current
        Column(modifier = Modifier.statusBarsPadding()) {
            TopBar()
            Button(onClick = {
                drawerController.open()
            }) {
                Text("测试按钮")
            }
        }
    }
}