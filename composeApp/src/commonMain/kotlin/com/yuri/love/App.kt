package com.yuri.love

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import com.yuri.love.views.HomeScreen
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import journal.composeapp.generated.resources.Res
import journal.composeapp.generated.resources.compose_multiplatform
import journal.composeapp.generated.resources.more

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