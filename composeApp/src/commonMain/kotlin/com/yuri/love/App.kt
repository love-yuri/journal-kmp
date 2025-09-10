package com.yuri.love

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import com.yuri.love.retrofit.initCurrentWeather
import com.yuri.love.views.home.HomeScreen
import com.yuri.love.views.home.TestScreen
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
    LaunchedEffect(Unit) {
        // init weather
        initCurrentWeather()
    }
}