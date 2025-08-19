package com.yuri.love

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import com.yuri.love.views.home.HomeScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

expect object Static {
    fun init()
}

@Composable
@Preview
fun App() {
    Static.init()
    MaterialTheme {
        Navigator(HomeScreen())
    }
}