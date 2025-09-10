package com.yuri.love.views.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.yuri.love.retrofit.WeatherService
import com.yuri.love.share.GlobalColors
import com.yuri.love.utils.platformSafeTopPadding
import com.yuri.love.views.home.components.DiaryHeaderAdvanced
import com.yuri.love.views.home.components.JournalCardComposable
import com.yuri.love.views.home.components.LovelyEnhancedDrawer
import com.yuri.love.views.home.components.TapBar
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 主页
 */
class HomeScreen: Screen {
    @Composable
    override fun Content() {
        CreateHome()
    }
}

@Preview
@Composable
private fun CreateHome() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // 抽屉内容
                LovelyEnhancedDrawer(
                    onCloseDrawer = {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
            }
        },
        content = {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(GlobalColors.tapBarBackground)
                .platformSafeTopPadding()
            ) {
                TapBar(scope, drawerState)
                
                Column(modifier = Modifier
                    .background(GlobalColors.softPinkGradient)
                    .padding( start = 10.dp, end = 10.dp)
                    .fillMaxSize()
                ) {
                    DiaryHeaderAdvanced()
                    JournalCardComposable()
                }
            }
        }
    )
}

