package com.yuri.love.share

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Yard
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.registry.ScreenProvider
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.navigator.Navigator
import com.yuri.love.Journal
import com.yuri.love.views.backup.BackupScreen
import com.yuri.love.views.create.CreateScreen
import com.yuri.love.views.home.HomeScreen
import com.yuri.love.views.setting.SettingScreen
import com.yuri.love.views.test.TestScreen
import com.yuri.love.views.webdav.WebdavScreen

/**
 * 导航管理
 */
object NavigatorManager {
    sealed class ScreenPageType: ScreenProvider {
        data object Home: ScreenPageType()
        data object Webdav: ScreenPageType()
        data object Test: ScreenPageType()
        data object Backup: ScreenPageType()
        data object Setting: ScreenPageType()
        data class Create(
            val journal: Journal? = null
        ): ScreenPageType()
    }

    data class EnhancedDrawerMenuItem (
        val key: ScreenPageType,
        val title: String,
        val icon: ImageVector,
    )

    // 默认页面类型
    val defaultPageType = ScreenPageType.Home

    // 页面堆栈
    private val pageStack = ArrayDeque<ScreenPageType>().apply {
        addLast(defaultPageType)
    }

    var currentPageType by mutableStateOf<ScreenPageType>(ScreenPageType.Home)

    // 全局导航
    private lateinit var navigator: Navigator

    // 当前侧边页面序号
    val drawerItems = listOf (
        EnhancedDrawerMenuItem(ScreenPageType.Home,"所有日记", Icons.Outlined.Home),
        EnhancedDrawerMenuItem(ScreenPageType.Webdav,"Webdav网盘", Icons.Outlined.Explore),
        EnhancedDrawerMenuItem(ScreenPageType.Backup, "备份和恢复", Icons.Outlined.Backup),
//        EnhancedDrawerMenuItem(ScreenPageType.Test, "测试页面", Icons.Outlined.Yard),
        EnhancedDrawerMenuItem(ScreenPageType.Setting, "系统设置", Icons.Outlined.Settings)
    )

    val featurePostsScreenModule = screenModule {
        register<ScreenPageType.Home> {
            HomeScreen()
        }
        register<ScreenPageType.Webdav> {
            WebdavScreen()
        }
        register<ScreenPageType.Backup> {
            BackupScreen()
        }
        register<ScreenPageType.Setting> {
            SettingScreen()
        }
        register<ScreenPageType.Test> {
            TestScreen()
        }
        register<ScreenPageType.Create> {
            CreateScreen(it.journal)
        }
    }

    /**
     * 初始化导航器
     */
    fun init(navigator: Navigator) {
        this.navigator = navigator
    }

    /**
     * push指定页面
     * @param type 页面类型
     */
    fun push(type: ScreenPageType) {
        val screen = ScreenRegistry.get(type)
        pageStack.addLast(type)
        navigator.push(screen)
        currentPageType = type
    }

    /**
     * 返回上一级
     */
    fun pop(): Boolean {
        if (!navigator.canPop) {
            return false
        }
        navigator.pop()
        if (pageStack.isNotEmpty()) {
            pageStack.removeLast()
            if (pageStack.isNotEmpty()) {
                currentPageType = pageStack.last()
            }
        }
        return true
    }
}