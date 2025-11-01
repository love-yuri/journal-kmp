package com.yuri.love.utils

import androidx.compose.ui.window.application
import com.yuri.love.utils.notification.Notification

actual object PlatformUtils {
    actual fun restart() {
    }

    actual fun toast(msg: String) {
        Notification.notificationState?.success(msg)
    }
}