package com.yuri.love

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Journal",
        state = WindowState().apply {
            size = DpSize(500.dp, 1000.dp)
            position = WindowPosition(Alignment.Center)
        }
    ) {
        App()
    }
}