package com.yuri.love

import cafe.adriel.voyager.core.registry.ScreenRegistry
import com.yuri.love.share.NavigatorManager.featurePostsScreenModule

actual object Static {
    actual fun init() {
        ScreenRegistry {
            featurePostsScreenModule()
        }
    }
}