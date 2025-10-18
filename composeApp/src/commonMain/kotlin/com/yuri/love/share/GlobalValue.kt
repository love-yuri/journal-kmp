package com.yuri.love.share

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Yard
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.yuri.love.views.home.HomeScreen
import com.yuri.love.views.test.TestScreen
import com.yuri.love.views.webdav.WebdavScreen
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 导航管理
 */
object NavigatorManager {
    enum class ScreenPageType {
        Home,
        Webdav,
        Test,
    }

    data class EnhancedDrawerMenuItem (
        val key: ScreenPageType,
        val title: String,
        val icon: ImageVector,
        val screen: () -> Screen,
    )

    // 默认页面类型
    val defaultPageType = ScreenPageType.Home

    // 默认页面
    val defaultScreen get() = createScreen(defaultPageType)

    // 页面堆栈
    private val pageStack = ArrayDeque<ScreenPageType>().apply {
        addLast(defaultPageType)
    }

    private val _currentPageType = MutableStateFlow(pageStack.last())
    val currentPageType: StateFlow<ScreenPageType> = _currentPageType.asStateFlow()

    // 全局导航
    private lateinit var navigator: Navigator

    // 当前侧边页面序号
    val drawerItems = listOf (
        EnhancedDrawerMenuItem(ScreenPageType.Home,"所有日记", Icons.Outlined.Home, { HomeScreen() }),
        EnhancedDrawerMenuItem(ScreenPageType.Webdav,"Webdav网盘", Icons.Outlined.Explore, { WebdavScreen() }),
//        EnhancedDrawerMenuItem("备份和恢复", Icons.Outlined.Favorite),
        EnhancedDrawerMenuItem(ScreenPageType.Test, "测试页面", Icons.Outlined.Yard, { TestScreen() }),
//        EnhancedDrawerMenuItem("系统设置", Icons.Outlined.Settings)
    )

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
        if (type == currentPageType.value) {
            return
        }
        navigator.push(createScreen(type))
        pageStack.addLast(type)
        _currentPageType.update { type }
    }

    /**
     * 返回上一级
     */
    fun pop() {
        if (!navigator.canPop) {
            return
        }
        navigator.pop()
        pageStack.removeLast()
        _currentPageType.update { pageStack.last() }
    }

    /**
     * 创建指定页面
     */
    fun createScreen(type: ScreenPageType) = getItem(type).screen()

    /**
     * 查找指定页面，如果没有找到则直接抛出异常
     * @param type 页面类型
     */
    fun getItem(type: ScreenPageType): EnhancedDrawerMenuItem {
        return drawerItems.find {
            it.key == type
        } ?: throw Exception("未找到指定页面: $type")
    }
}

object GlobalValue {
    // 当前天气
    var weather by mutableStateOf("loading...")

    // 导航管理
    val navigatorManager = NavigatorManager
}