package com.yuri.love

import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Journal",
        state = WindowState().apply {
            position = WindowPosition(Alignment.Center)
        }
    ) {
        App()
    }
}