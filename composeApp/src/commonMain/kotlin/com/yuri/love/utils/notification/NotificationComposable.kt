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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview



@Preview
@Composable
private fun NotificationComposable(
    notification: Notification = Notification(
        id = 1,
        content = "这是一条通知",
    ),
    onCompleted: () -> Unit = {}
) {
    val process = remember { Animatable(0.3f) }

    LaunchedEffect(Unit) {
        process.animateTo(
            1f,
            animationSpec = tween(
                easing = LinearEasing,
                durationMillis = 3000
            )
        )
        onCompleted()
    }

    Box(modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .fillMaxWidth(0.6f)
        .height(40.dp)
        .background(color = Color(0xfaFF99CC))
    ) {
        Text(notification.content,
            modifier = Modifier
                .align(Alignment.Center),
            color = Color.White,
            fontSize = 20.sp
        )
        LinearProgressIndicator(
            modifier = Modifier
                .clip(RoundedCornerShape(0.dp))
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
            ,
            progress = { process.value },
            trackColor = Color.Transparent
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