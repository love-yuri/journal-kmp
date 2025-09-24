package com.yuri.love.utils.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember

data class Notification(
    val id: Long,
    val content: String = "this is a tip!",
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

    fun removeNotification(id: Long) {
        _notifications.removeAll { it.id == id }
    }
}

@Composable
fun rememberNotificationState(): NotificationState {
    return remember { NotificationState() }
}