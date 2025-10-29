package com.yuri.love.views.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.yuri.love.components.SimpleTopBar
import com.yuri.love.share.GlobalStyle
import com.yuri.love.share.GlobalValue
import com.yuri.love.share.NavigatorManager.ScreenPageType

class SettingScreen : Screen {
    @Composable
    override fun Content() {
        var fingerprintEnabled by remember { mutableStateOf(false) }
        var pinLoginEnabled by remember { mutableStateOf(false) }
        var autoBackupEnabled by remember { mutableStateOf(true) }
        var notificationEnabled by remember { mutableStateOf(true) }
        var darkModeEnabled by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFF5F8),
                            Color(0xFFFFFAFD)
                        )
                    )
                )
                .statusBarsPadding()
        ) {
            SimpleTopBar()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 安全设置区域
                SettingSection(title = "安全设置") {
                    SettingItemSwitch(
                        icon = Icons.Default.Fingerprint,
                        title = "指纹认证",
                        subtitle = "使用指纹快速解锁应用",
                        checked = fingerprintEnabled,
                        onCheckedChange = { fingerprintEnabled = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingItemSwitch(
                        icon = Icons.Default.Pin,
                        title = "PIN码登录",
                        subtitle = "使用PIN码作为备用登录方式",
                        checked = pinLoginEnabled,
                        onCheckedChange = { pinLoginEnabled = it }
                    )
                }

                // 数据管理区域
                SettingSection(title = "数据管理") {
                    SettingItemSwitch(
                        icon = Icons.Default.CloudSync,
                        title = "关闭时自动备份",
                        subtitle = "退出应用时自动同步备份数据",
                        checked = autoBackupEnabled,
                        onCheckedChange = { autoBackupEnabled = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingItemButton(
                        icon = Icons.Default.Backup,
                        title = "立即备份",
                        subtitle = "手动备份当前数据",
                    ) {
                        GlobalValue.navigatorManager.push(ScreenPageType.Backup)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingItemButton(
                        icon = Icons.Default.Restore,
                        title = "恢复数据",
                        subtitle = "从备份恢复数据"
                    ) {
                        GlobalValue.navigatorManager.push(ScreenPageType.Backup)
                    }
                }

                // 通知设置区域
                SettingSection(title = "通知设置") {
                    SettingItemSwitch(
                        icon = Icons.Default.Notifications,
                        title = "消息通知",
                        subtitle = "接收重要消息提醒",
                        checked = notificationEnabled,
                        onCheckedChange = { notificationEnabled = it }
                    )
                }

                // 外观设置区域
                SettingSection(title = "外观设置") {
                    SettingItemSwitch(
                        icon = Icons.Default.DarkMode,
                        title = "深色模式",
                        subtitle = "切换至深色主题",
                        checked = darkModeEnabled,
                        onCheckedChange = { darkModeEnabled = it }
                    )
                }

                // 关于区域
                SettingSection(title = "关于") {
                    SettingItemButton(
                        icon = Icons.Default.Info,
                        title = "版本信息",
                        subtitle = "v1.0.0"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingItemButton(
                        icon = Icons.Default.PrivacyTip,
                        title = "隐私政策",
                        subtitle = "查看隐私保护条款"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun SettingSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFB8748D),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingItemSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)) // 圆角裁剪涟漪效果
            .clickable(
                onClick = { onCheckedChange(!checked) },
                indication = GlobalStyle.ripple,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFD4E5),
                                Color(0xFFFFE4EE)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFE8739F),
                    modifier = Modifier.size(22.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF3D3D3D)
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFFA5A5A5),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFFFFB3C6),
                checkedBorderColor = Color.Transparent,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFE0E0E0),
                uncheckedBorderColor = Color.Transparent,
                disabledCheckedThumbColor = Color.White,
                disabledCheckedTrackColor = Color(0xFFFFB3C6),
                disabledCheckedBorderColor = Color.Transparent,
                disabledUncheckedThumbColor = Color.White,
                disabledUncheckedTrackColor = Color(0xFFE0E0E0),
                disabledUncheckedBorderColor = Color.Transparent
            ),
            enabled = false
        )
    }
}

@Composable
private fun SettingItemButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick : () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)) // 圆角裁剪涟漪效果
            .clickable(
                onClick = onClick,
                indication = GlobalStyle.ripple,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    )  {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFFD4E5),
                                    Color(0xFFFFE4EE)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFFE8739F),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3D3D3D)
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color(0xFFA5A5A5),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFD0D0D0),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}