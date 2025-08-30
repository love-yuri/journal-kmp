package com.yuri.love.views.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random

data class EnhancedDrawerMenuItem(
    val title: String,
    val icon: ImageVector,
    val filledIcon: ImageVector,
    val isSelected: Boolean = false,
    val badge: Int? = null,
    val hasNewContent: Boolean = false
)

@Preview
@Composable
fun LovelyEnhancedDrawer(onCloseDrawer: () -> Unit = {}) {
    val drawerItems = listOf(
        EnhancedDrawerMenuItem("Home", Icons.Outlined.Home, Icons.Filled.Home, true),
        EnhancedDrawerMenuItem("Discover", Icons.Outlined.Explore, Icons.Filled.Explore, hasNewContent = true),
        EnhancedDrawerMenuItem("Messages", Icons.Outlined.ChatBubbleOutline, Icons.Filled.ChatBubble, badge = 8),
        EnhancedDrawerMenuItem("Matches", Icons.Outlined.Favorite, Icons.Filled.Favorite, badge = 3),
        EnhancedDrawerMenuItem("Profile", Icons.Outlined.Person, Icons.Filled.Person),
        EnhancedDrawerMenuItem("Premium", Icons.Outlined.Diamond, Icons.Filled.Diamond, hasNewContent = true),
        EnhancedDrawerMenuItem("Settings", Icons.Outlined.Settings, Icons.Filled.Settings)
    )

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp)
    ) {
        // 梦幻背景
        EnhancedBackground()

        // 浮动心形装饰
        FloatingHearts()

        // 主要内容
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            EnhancedDrawerHeader()

            Spacer(Modifier.height(32.dp))

            // 导航菜单
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "NAVIGATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B5A96),
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
                    )
                }

                items(drawerItems.size) { index ->
                    val item = drawerItems[index]
                    EnhancedDrawerItem(
                        item = item,
                        index = index,
                        onClick = onCloseDrawer
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            EnhancedDrawerFooter()
        }
    }
}

@Composable
private fun EnhancedBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        // 主背景渐变
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xFFFFFAFD),
                            Color(0xFFFFF0F7),
                            Color(0xFFFFE8F2),
                            Color(0xFFFEE1ED)
                        )
                    )
                )
        )

        // 动态光晕效果
        val infiniteTransition = rememberInfiniteTransition(label = "background")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(20000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )

        Box(
            modifier = Modifier
                .size(300.dp)
                .offset((-80).dp, 50.dp)
                .rotate(rotation)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0x25FF6B9D),
                            Color(0x15FFB3D1),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(200.dp, 300.dp)
                .rotate(-rotation * 0.7f)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0x20D946EF),
                            Color(0x10FFB3D1),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // 渐变覆盖层增强深度
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            Color(0x08FF6B9D)
                        )
                    )
                )
        )
    }
}

@Composable
private fun EnhancedDrawerHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x15FFFFFF)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 头像部分
            Box {
                // 发光边框
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    Color(0xFFFF6B9D),
                                    Color(0xFFD946EF),
                                    Color(0xFF8B5CF6),
                                    Color(0xFFFF6B9D)
                                )
                            ),
                            CircleShape
                        )
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White, CircleShape)
                            .padding(3.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFFFFB3D1),
                                        Color(0xFFFF6B9D)
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Y",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // 在线状态指示器
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.BottomEnd)
                        .offset((-2).dp, (-2).dp)
                        .background(Color.White, CircleShape)
                        .padding(2.dp)
                        .background(Color(0xFF34C759), CircleShape)
                )

                // 心形装饰
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.TopEnd)
                        .offset(2.dp, (-2).dp)
                        .drawWithContent {
                            drawHeart(Color(0xFFFF6B9D), size.minDimension)
                        }
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Yuri Love",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D1B35)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    Icons.Filled.Diamond,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    "Premium Member",
                    fontSize = 13.sp,
                    color = Color(0xFF8B5A96),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(12.dp))

            // 统计卡片
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("Matches", "128", Modifier.weight(1f))
                StatCard("Likes", "1.2K", Modifier.weight(1f))
                StatCard("Views", "5.6K", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(
                Color(0x10FFFFFF),
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D1B35)
        )
        Text(
            label,
            fontSize = 11.sp,
            color = Color(0xFF8B5A96)
        )
    }
}

@Composable
private fun EnhancedDrawerItem(
    item: EnhancedDrawerMenuItem,
    index: Int,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    // 进入动画
    val animatedVisibility = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(index * 50L)
        animatedVisibility.animateTo(
            1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale * animatedVisibility.value)
            .alpha(animatedVisibility.value),
        color = when {
            item.isSelected -> Color(0x25FF6B9D)
            isPressed -> Color(0x15FF6B9D)
            else -> Color.Transparent
        },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        }
                    )
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标容器
            Box {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            if (item.isSelected) {
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFFFF6B9D),
                                        Color(0xFFD946EF)
                                    )
                                )
                            } else {
                                Brush.linearGradient(
                                    listOf(
                                        Color(0x15FF6B9D),
                                        Color(0x20FFB3D1)
                                    )
                                )
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (item.isSelected) item.filledIcon else item.icon,
                        contentDescription = item.title,
                        tint = if (item.isSelected) Color.White else Color(0xFF8B5A96),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // 新内容指示器
                if (item.hasNewContent && !item.isSelected) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .align(Alignment.TopEnd)
                            .background(Color(0xFFFF4757), CircleShape)
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = if (item.isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (item.isSelected) Color(0xFF2D1B35) else Color(0xFF4A5568)
                )

                if (item.title == "Premium") {
                    Text(
                        "Unlock exclusive features",
                        fontSize = 12.sp,
                        color = Color(0xFF8B5A96)
                    )
                }
            }

            // 右侧指示器
            when {
                item.badge != null && item.badge > 0 -> {
                    Badge(item.badge)
                }
                item.isSelected -> {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFFF6B9D), CircleShape)
                    )
                }
                else -> {
                    Icon(
                        Icons.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFFB0B7C3),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Badge(count: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "badge")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "badgeScale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFFFF4757),
                        Color(0xFFFF3742)
                    )
                ),
                CircleShape
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (count > 99) "99+" else count.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun EnhancedDrawerFooter() {
    Column {
        // 分隔线
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            Color(0x30FF6B9D),
                            Color.Transparent
                        )
                    )
                )
        )

        Spacer(Modifier.height(20.dp))

        // 快捷操作
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickActionButton(
                icon = Icons.Filled.Palette,
                label = "Theme",
                onClick = { }
            )

            QuickActionButton(
                icon = Icons.Filled.Help,
                label = "Help",
                onClick = { }
            )

            QuickActionButton(
                icon = Icons.Filled.Logout,
                label = "Logout",
                onClick = { }
            )
        }

        Spacer(Modifier.height(16.dp))

        // 版本信息
        Text(
            text = "Lovely Hearts v2.5.0",
            fontSize = 12.sp,
            color = Color(0xFF8B5A96),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    Color(0x15FF6B9D),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = Color(0xFF8B5A96),
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(Modifier.height(4.dp))

        Text(
            label,
            fontSize = 10.sp,
            color = Color(0xFF8B5A96)
        )
    }
}