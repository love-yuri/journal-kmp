package com.yuri.love

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.yuri.love.retrofit.initCurrentWeather
import com.yuri.love.share.GlobalStyle
import com.yuri.love.share.GlobalValue
import com.yuri.love.utils.notification.NotificationContainer
import com.yuri.love.utils.platformSafeTopPadding
import com.yuri.love.views.home.components.DrawerController
import com.yuri.love.views.home.components.HomeDrawer
import com.yuri.love.views.home.components.LocalDrawerController
import org.jetbrains.compose.ui.tooling.preview.Preview

expect object Static {
    fun init()
}

@Composable
@Preview
fun App() {
    Static.init()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val drawerController = remember { DrawerController(drawerState, scope) }
    CompositionLocalProvider(LocalDrawerController provides drawerController) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                HomeDrawer(
                    onCloseDrawer = {
                        drawerController.close()
                    }
                )
            },
            content = {
                MaterialTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(GlobalStyle.softPinkGradient)
                    ) {
                        Navigator(
                            screen = GlobalValue.navigatorManager.defaultScreen,
                            onBackPressed = {
                                GlobalValue.navigatorManager.pop()
                                false
                            }
                        ) { navigator ->
                            GlobalValue.navigatorManager.init(navigator)
                            SlideTransition(navigator)
                        }
                        NotificationContainer(
                            Modifier
                                .align(Alignment.TopCenter)
                                .platformSafeTopPadding()
                        )
                    }
                }
            },
        )
    }

    LaunchedEffect(Unit) {
        // init weather
        initCurrentWeather()
    }
}