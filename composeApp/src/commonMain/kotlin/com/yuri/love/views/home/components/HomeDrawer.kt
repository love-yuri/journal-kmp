package com.yuri.love.views.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuri.love.styles.GlobalFonts
import journal.composeapp.generated.resources.Res
import journal.composeapp.generated.resources.avatar
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.painterResource

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
        EnhancedDrawerMenuItem("Matches", Icons.Outlined.Favorite, Icons.Filled.Favorite, badge = 3),
        EnhancedDrawerMenuItem("Profile", Icons.Outlined.Person, Icons.Filled.Person),
        EnhancedDrawerMenuItem("Settings", Icons.Outlined.Settings, Icons.Filled.Settings)
    )

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(380.dp)
    ) {
        // 梦幻背景
        EnhancedBackground()

        // 樱花特效
        SakuraAnimation()

        // 主要内容
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            EnhancedDrawerHeader()

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
                .rotate(rotation)
                .size(300.dp)
                .offset((-80).dp, 50.dp)
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
                        Image(
                            painter = painterResource(Res.drawable.avatar),
                            null,
                            modifier = Modifier.fillMaxSize()
                                .clip(CircleShape)
                                .scale(1.5f),
                        )
                    }
                }
                
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
                "Love Yuri",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D1B35)
            )

            // 统计卡片
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val modifier = Modifier.weight(1f)
                StatCard("总文章", "128", modifier)
                StatCard("总字数", "1.2K", modifier)
                StatCard("总时间", "2年", modifier)
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
            fontSize = 12.sp,
            color = Color(0xFF8B5A96),
            fontFamily = GlobalFonts.MapleMonoFont
        )
    }
}
@Composable
private fun EnhancedDrawerItem(
    item: EnhancedDrawerMenuItem,
    index: Int,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = when {
            item.isSelected -> Brush.horizontalGradient(
                listOf(
                    Color(0x30FF6B9D),
                    Color(0x20D946EF)
                )
            ).let { Color(0x25FF6B9D) }
            else -> Color.Transparent
        },
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧装饰条
            if (item.isSelected) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(24.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFFFF6B9D),
                                    Color(0xFFD946EF)
                                )
                            ),
                            RoundedCornerShape(2.dp)
                        )
                )
                Spacer(Modifier.width(16.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 17.sp,
                    fontWeight = if (item.isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (item.isSelected) Color(0xFF2D1B35) else Color(0xFF4A5568),
                    letterSpacing = 0.3.sp
                )
            }

            // 右侧指示器
            when {
                item.isSelected -> {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        Color(0xFFFF6B9D),
                                        Color(0xFFD946EF)
                                    )
                                ),
                                CircleShape
                            )
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                Color(0x10B0B7C3),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color(0xFFB0B7C3),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
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

        // 版本信息
        Text(
            text = "心情日记 v1.0.0",
            fontSize = 12.sp,
            color = Color(0xFF8B5A96),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}