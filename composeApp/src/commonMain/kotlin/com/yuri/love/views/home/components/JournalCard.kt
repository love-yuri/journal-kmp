package com.yuri.love.views.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.yuri.love.Journal
import com.yuri.love.components.DeleteConfirmDialog
import com.yuri.love.share.GlobalStyle
import com.yuri.love.utils.TimeUtils.formatTimestampDay
import com.yuri.love.utils.TimeUtils.formatTimestampTime
import com.yuri.love.views.create.CreateScreen
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import journal.composeapp.generated.resources.Res
import journal.composeapp.generated.resources.date
import journal.composeapp.generated.resources.weather_sun
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 优化版日记卡片 - 现代化设计 + 长按删除
 */
@Composable
@Preview
fun JournalCardComposable(
    journal: Journal = Journal(
        id = 1,
        title = "今天是美好的一天，阳光明媚",
        content = "22",
        createdAt = 1757673445059,
        updatedAt = 1757673445059,
        mood = "开心",
        weather = "晴天"
    ),
    onDelete: ((Journal) -> Unit)? = null
) {
    val navigator = LocalNavigator.current

    // 动画状态
    var isPressed by remember { mutableStateOf(false) }

    // 缩放动画
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    // 阴影动画
    val shadowElevation by animateFloatAsState(
        targetValue = if (isPressed) 4.dp.value else 12.dp.value,
        animationSpec = tween(durationMillis = 150),
        label = "shadow"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .scale(scale)
            .shadow(
                elevation = shadowElevation.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color(0xFFFF69B4).copy(alpha = 0.25f),
                ambientColor = Color(0xFFFFB3D1).copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        },
                        onTap = {
                            navigator?.push(CreateScreen(journal))
                        },
                        onLongPress = {
                            logger {}.info { "长按: ${journal.title}" }
                            onDelete?.invoke(journal)
                        }
                    )
                }
                .background(
                    brush = if (isPressed) {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFFE4E1),
                                Color(0xFFFFF0F5)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White,
                                Color(0xFFFFFAFA)
                            )
                        )
                    },
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            // 装饰性渐变条
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFF69B4),
                                Color(0xFFFFB3D1),
                                Color(0xFFFFC0CB)
                            )
                        ),
                        shape = RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp)
                    )
                    .align(Alignment.CenterStart)
            )

            Column(
                modifier = Modifier.padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
            ) {
                // 标题和状态指示器
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = journal.title?.takeIf { it.isNotEmpty() } ?: journal.content.take(20),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        color = Color(0xFF2D1B4E),
                        fontFamily = GlobalStyle.MapleMonoFont,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // 心情和状态指示器
                    MoodIndicator(
                        mood = journal.mood,
                        isActive = isPressed
                    )
                }

                // 分割线
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = Color(0xFFFFE4E1)
                )

                // 元信息行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 日期和天气
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MetaInfoItem(
                            icon = Res.drawable.date,
                            text = formatTimestampDay(journal.createdAt),
                            tint = Color(0xFFFF69B4)
                        )

                        if (!journal.weather.isNullOrBlank()) {
                            Spacer(modifier = Modifier.width(16.dp))
                            MetaInfoItem(
                                icon = Res.drawable.weather_sun,
                                text = journal.weather,
                                tint = Color(0xFFFFA500)
                            )
                        }
                    }

                    // 时间
                    Text(
                        text = formatTimestampTime(journal.createdAt),
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF),
                        fontWeight = FontWeight.Medium,
                        fontFamily = GlobalStyle.MapleMonoFont
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 标签区域
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedTag(text = "#生活", color = Color(0xFF8B5CF6), isPressed = isPressed)
                    AnimatedTag(text = "#日记", color = Color(0xFFEC4899), isPressed = isPressed)
                    if (!journal.mood.isNullOrBlank()) {
                        AnimatedTag(text = "#${journal.mood}", color = Color(0xFF06B6D4), isPressed = isPressed)
                    }
                }
            }
        }
    }
}

/**
 * 心情指示器
 */
@Composable
fun MoodIndicator(
    mood: String?,
    isActive: Boolean = false
) {
    val pulseScale = remember { Animatable(1f) }

    LaunchedEffect(isActive) {
        if (isActive) {
            pulseScale.animateTo(
                targetValue = 1.2f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                )
            )
            pulseScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    val emoji = when (mood) {
        "开心", "快乐", "愉快" -> "😊"
        "难过", "伤心", "沮丧" -> "😢"
        "生气", "愤怒", "烦躁" -> "😤"
        "平静", "安静", "宁静" -> "😌"
        "兴奋", "激动", "刺激" -> "🤩"
        "疲惫", "累", "困" -> "😴"
        else -> "🌸"
    }

    Surface(
        shape = CircleShape,
        color = Color(0xFFFFE4E1),
        modifier = Modifier
            .size(24.dp)
            .graphicsLayer {
                scaleX = pulseScale.value
                scaleY = pulseScale.value
            }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = emoji,
                fontSize = 16.sp
            )
        }
    }
}

/**
 * 元信息项组件
 */
@Composable
fun MetaInfoItem(
    icon: org.jetbrains.compose.resources.DrawableResource,
    text: String,
    tint: Color = Color.Gray
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = tint.copy(alpha = 0.1f),
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Text(
            text = text,
            fontSize = 13.sp,
            color = Color(0xFF6B7280),
            fontWeight = FontWeight.Medium,
            fontFamily = GlobalStyle.MapleMonoFont
        )
    }
}

/**
 * 带动画的标签 - 优化版
 */
@Composable
fun AnimatedTag(
    text: String,
    color: Color,
    isPressed: Boolean
) {
    val tagScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "tagScale"
    )

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        modifier = Modifier
            .scale(tagScale)
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}