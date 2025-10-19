package com.yuri.love.utils.notification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NotificationIcon(type: NotificationType, modifier: Modifier = Modifier) {
    val icon = when (type) {
        NotificationType.Info -> "ℹ"
        NotificationType.Success -> "✓"
        NotificationType.Warning -> "⚠"
        NotificationType.Error -> "✕"
    }

    Text(
        text = icon,
        modifier = modifier,
        color = NotificationColors.iconColor,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun NotificationComposable(
    notification: Notification,
    onCompleted: () -> Unit = {}
) {
    val process = remember { Animatable(0f) }

    // Get colors based on notification type
    val backgroundColor = when (notification.type) {
        NotificationType.Info -> NotificationColors.infoBg
        NotificationType.Success -> NotificationColors.successBg
        NotificationType.Warning -> NotificationColors.warningBg
        NotificationType.Error -> NotificationColors.errorBg
    }

    val progressColor = when (notification.type) {
        NotificationType.Info -> NotificationColors.infoProgress
        NotificationType.Success -> NotificationColors.successProgress
        NotificationType.Warning -> NotificationColors.warningProgress
        NotificationType.Error -> NotificationColors.errorProgress
    }

    val animationDuration = when (notification.type) {
        NotificationType.Error -> 4000 // Errors stay longer
        NotificationType.Warning -> 3000 // Warnings stay a bit longer
        else -> 2000 // Info and Success
    }

    LaunchedEffect(notification.id) {
        process.animateTo (
            1f,
            animationSpec = tween(
                easing = LinearEasing,
                durationMillis = animationDuration
            )
        )
        onCompleted()
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth(0.7f)
            .height(38.dp)
            .background(color = backgroundColor.copy(alpha = 0.95f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Icon
            NotificationIcon(
                type = notification.type,
                modifier = Modifier.padding(end = 8.dp)
            )

            // Content
            Text(
                text = notification.content,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }

        // Progress bar at the bottom
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .align(Alignment.BottomCenter),
            progress = { process.value },
            color = progressColor,
            trackColor = Color.Transparent
        )
    }
}

@Preview()
@Composable
fun PreviewAllNotificationTypes() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        NotificationComposable(
            notification = Notification(
                id = 1,
                content = "信息提示",
                type = NotificationType.Info
            )
        )

        NotificationComposable(
            notification = Notification(
                id = 2,
                content = "操作成功",
                type = NotificationType.Success
            )
        )

        NotificationComposable(
            notification = Notification(
                id = 3,
                content = "警告信息",
                type = NotificationType.Warning
            )
        )

        NotificationComposable(
            notification = Notification(
                id = 4,
                content = "错误提示",
                type = NotificationType.Error
            )
        )
    }
}

@Composable
private fun NotificationComposable(
    notification: Notification,
    isRemoving: Boolean,
    onRemove: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    // 在首次组合时启动进入动画
    LaunchedEffect(Unit) {
        isVisible = true
    }

    // 当开始移除时，设置为不可见
    LaunchedEffect(isRemoving) {
        if (isRemoving) {
            isVisible = false
        }
    }

    val offsetY by animateFloatAsState(
        targetValue = if (isVisible) 0f else -30f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    // 添加弹性缩放效果
    val scale by animateFloatAsState(
        targetValue = if (isRemoving) 0.8f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    // 旋转效果
    val rotation by animateFloatAsState(
        targetValue = if (isRemoving) 10f else 0f,
        animationSpec = tween(200)
    )

    AnimatedVisibility(
        visible = !isRemoving,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(
            animationSpec = tween(300, delayMillis = 100)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it / 2 },
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ) + shrinkVertically(
            shrinkTowards = Alignment.CenterVertically,
            animationSpec = tween(300)
        ) + fadeOut(
            animationSpec = tween(200)
        )
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    rotationZ = rotation
                    translationY = offsetY
                }
        ) {
            NotificationComposable(
                notification,
                onRemove
            )
        }
    }
}

@Composable
fun NotificationContainer(modifier: Modifier) {
    val notificationState = rememberNotificationState()
    val list = notificationState.notifications
    val removingItems = remember { mutableStateOf(setOf<Long>()) }

    LaunchedEffect(notificationState) {
        Notification.notificationState = notificationState
    }

    Box(modifier = modifier) {
        LazyColumn {
            items(list, key = { it.id }) { item ->
                NotificationComposable(
                    notification = item,
                    isRemoving = removingItems.value.contains(item.id),
                ) {
                    // 开始移除动画
                    removingItems.value += item.id

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(250) // 等待退出动画完成
                        Notification.notificationState?.removeNotification(item.id)
                        removingItems.value -= item.id
                    }
                }
                // 只在通知可见时才显示间距
                if (!removingItems.value.contains(item.id)) {
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
        }
    }
}