package com.yuri.love

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import com.yuri.love.retrofit.initCurrentWeather
import com.yuri.love.utils.notification.NotificationContainer
import com.yuri.love.views.home.HomeScreen
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.jetbrains.compose.ui.tooling.preview.Preview

expect object Static {
    fun init()
}

@Composable
@Preview
fun App() {
    Static.init()
    val log = logger { }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Navigator(HomeScreen())
            NotificationContainer(Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-10).dp)
            )
        }
    }.also {
        LaunchedEffect(Unit) {
            // init weather
            initCurrentWeather()
        }
    }
}