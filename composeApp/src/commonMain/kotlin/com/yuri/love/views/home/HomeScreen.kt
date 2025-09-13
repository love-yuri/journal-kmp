package com.yuri.love.views.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import com.yuri.love.Journal
import com.yuri.love.flow.JournalFlow
import com.yuri.love.share.GlobalColors
import com.yuri.love.utils.platformSafeTopPadding
import com.yuri.love.views.home.components.DiaryHeaderAdvanced
import com.yuri.love.views.home.components.JournalCardComposable
import com.yuri.love.views.home.components.LovelyEnhancedDrawer
import com.yuri.love.views.home.components.TapBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 主页 - 优化启动体验版本
 */
class HomeScreen: Screen {
    @Composable
    override fun Content() {
        // 使用viewModel()委托来创建和管理ViewModel
        val journalViewModel: JournalFlow = viewModel()

        // 收集StateFlow状态
        val journals by journalViewModel.journals.collectAsState()

        CreateHome(journals)
    }
}


@Preview
@Composable
private fun CreateHome(journals: List<Journal>) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var showContent by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    // 数据状态
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GlobalColors.softPinkGradient)
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(GlobalColors.tapBarBackground)
                        .platformSafeTopPadding()
                ) {
                    // TapBar
                    TapBar(scope, drawerState)

                    // 主内容区域
                    Column(
                        modifier = Modifier
                            .background(GlobalColors.softPinkGradient)
                            .padding(start = 10.dp, end = 10.dp)
                            .fillMaxSize()
                    ) {
                        DiaryHeaderAdvanced()
                        AnimatedVisibility(visible = showContent) {
                            LazyColumn {
                                items(journals) { journal ->
                                    JournalCardComposable(journal)
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}
