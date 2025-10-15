package com.yuri.love.share

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

object GlobalValue {
    var weather by mutableStateOf("loading...")
    private var _navigator: Navigator? = null

    fun bindNavigator(navigator: Navigator) {
        _navigator = navigator
    }

    fun push(screen: Screen) {
        _navigator?.push(screen)
    }
}