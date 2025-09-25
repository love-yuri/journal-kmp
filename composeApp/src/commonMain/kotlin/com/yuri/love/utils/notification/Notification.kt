package com.yuri.love.utils.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.yuri.love.utils.algorithm.SnowFlake

enum class NotificationType {
    Info,
    Success,
    Warning,
    Error
}

object NotificationColors {
    // Background colors
    val infoBg = Color(0xFF2196F3)
    val successBg = Color(0xFF4CAF50)
    val warningBg = Color(0xFFFF9800)
    val errorBg = Color(0xFFF44336)

    // Progress bar colors (slightly darker)
    val infoProgress = Color(0xFF1976D2)
    val successProgress = Color(0xFF388E3C)
    val warningProgress = Color(0xFFF57C00)
    val errorProgress = Color(0xFFD32F2F)

    // Icon colors (can be white or adjusted as needed)
    val iconColor = Color.White
}

data class Notification(
    val id: Long,
    val content: String = "this is a tip!",
    val type: NotificationType = NotificationType.Info
) {
    companion object {
        var notificationState: NotificationState? = null
    }
}

class NotificationState {
    private val _notifications = mutableStateListOf<Notification>()
    val notifications: MutableList<Notification> get() = _notifications

    fun addNotification(notification: Notification) {
        _notifications.add(notification)
    }

    fun success(content: String) {
        addNotification(Notification(
            id = SnowFlake.nextId(),
            content = content,
            type = NotificationType.Success
        ))
    }

    fun error(content: String) {
        addNotification(Notification(
            id = SnowFlake.nextId(),
            content = content,
            type = NotificationType.Error
        ))
    }

    fun removeNotification(id: Long) {
        _notifications.removeAll { it.id == id }
    }
}

@Composable
fun rememberNotificationState(): NotificationState {
    return remember { NotificationState() }
}