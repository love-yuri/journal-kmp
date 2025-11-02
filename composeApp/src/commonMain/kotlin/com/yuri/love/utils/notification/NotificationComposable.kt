package com.yuri.love.utils.notification

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
        process.animateTo(
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
            .fillMaxWidth()
            .padding(horizontal = 16.dp) // 添加左右边距，避免贴边
    ) {
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth()
                .background(color = backgroundColor.copy(alpha = 0.96f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp), // 增加内边距
                verticalAlignment = Alignment.Top, // 改为顶部对齐，适配多行文本
                horizontalArrangement = Arrangement.Start
            ) {
                // Icon - 固定在顶部
                NotificationIcon(
                    type = notification.type,
                    modifier = Modifier
                        .padding(end = 12.dp, top = 2.dp) // 图标顶部微调
                )

                // Content - 支持多行
                Text(
                    text = notification.content,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    lineHeight = 20.sp, // 设置行高
                    maxLines = 3, // 最多3行
                    overflow = TextOverflow.Ellipsis // 超出显示省略号
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
}

@Preview()
@Composable
fun PreviewAllNotificationTypes() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(vertical = 16.dp),
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
                content = "操作成功完成，所有数据已保存",
                type = NotificationType.Success
            )
        )

        NotificationComposable(
            notification = Notification(
                id = 3,
                content = "警告：您的存储空间即将用完，请及时清理不必要的文件以确保系统正常运行",
                type = NotificationType.Warning
            )
        )

        NotificationComposable(
            notification = Notification(
                id = 4,
                content = "错误：网络连接失败，请检查您的网络设置后重试",
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

    // 优化缩放效果
    val scale by animateFloatAsState(
        targetValue = if (isRemoving) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    // 旋转效果
    val rotation by animateFloatAsState(
        targetValue = if (isRemoving) 3f else 0f,
        animationSpec = tween(200)
    )

    // 退出时的透明度动画
    val alpha by animateFloatAsState(
        targetValue = if (isRemoving) 0f else 1f,
        animationSpec = tween(250)
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
            targetOffsetY = { -it / 2 }, // 向上滑出
            animationSpec = tween(250, easing = FastOutSlowInEasing)
        ) + shrinkVertically(
            shrinkTowards = Alignment.Top, // 从顶部收缩
            animationSpec = tween(250)
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
                    this.alpha = alpha
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
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp), // 使用统一间距
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
            }
        }
    }
}