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
import com.yuri.love.database.JournalService
import com.yuri.love.database.SystemConfig
import com.yuri.love.share.AppVersion
import com.yuri.love.share.GlobalStyle
import com.yuri.love.utils.notification.Notification
import com.yuri.love.components.ConfirmDialogInfo

class SettingScreen : Screen {
    var confirmDialogInfo by mutableStateOf<ConfirmDialogInfo>(ConfirmDialogInfo(
        visible = false,
        title = "",
        message = ""
    ))

    @Composable
    override fun Content() {
        var fingerprintEnabled by remember { mutableStateOf(SystemConfig.FingerprintEnabled) }
        var pinLoginEnabled by remember { mutableStateOf(SystemConfig.PinLoginEnabled) }
        var autoBackupEnabled by remember { mutableStateOf(SystemConfig.AutoBackup) }
        var notificationEnabled by remember { mutableStateOf(true) }
        var darkModeEnabled by remember { mutableStateOf(false) }

        ConfirmDialog()

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
                    ) {
                        PlatformSettings.setFingerprintEnabled(it) { res ->
                            if (res == it) {
                                fingerprintEnabled = res
                                SystemConfig.FingerprintEnabled = res
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingItemSwitch(
                        icon = Icons.Default.Pin,
                        title = "PIN码登录",
                        subtitle = "使用PIN码作为备用登录方式",
                        checked = pinLoginEnabled,
                    ) {
                        PlatformSettings.setPinLoginEnabled(it) { res ->
                            if (res == it) {
                                pinLoginEnabled = res
                                SystemConfig.PinLoginEnabled = res
                            }
                        }
                    }
                }

                // 数据管理区域
                SettingSection(title = "数据管理") {
                    SettingItemSwitch(
                        icon = Icons.Default.CloudSync,
                        title = "Webdav自动备份",
                        subtitle = "修改数据时同步备份数据到Webdav",
                        checked = autoBackupEnabled
                    ) {
                        autoBackupEnabled = it
                        SystemConfig.AutoBackup = it
                        Notification.notificationState?.success("自动备份已 ${if (it) "开启" else "关闭"}")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingItemButton(
                        icon = Icons.Default.Backup,
                        title = "立即备份到远程",
                        subtitle = "手动备份当前数据到远程服务器",
                    ) {
                        confirmDialogInfo = ConfirmDialogInfo(
                            visible = true,
                            title = "是否立即备份",
                            message = "备份将会替换远程已有数据!"
                        ) {
                            if (!SystemConfig.isLoggedIn) {
                                Notification.notificationState?.error("请先登录!")
                                return@ConfirmDialogInfo
                            }
                            JournalService.autoBackup()
                            Notification.notificationState?.success("已同步到远程")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingItemButton(
                        icon = Icons.Default.Restore,
                        title = "立即同步远程备份",
                        subtitle = "从远程服务器同步备份数据"
                    ) {
                        confirmDialogInfo = ConfirmDialogInfo(
                            visible = true,
                            title = "是否立即同步",
                            message = "同步将会覆盖本地已有数据!"
                        ) {
                            JournalService.restoreFromAutoBackup()
                        }
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
                        subtitle = AppVersion
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


    @Composable
    private fun ConfirmDialog() {
        com.yuri.love.components.ConfirmDialog(
            visible = confirmDialogInfo.visible,
            title = confirmDialogInfo.title,
            message = confirmDialogInfo.message,
            onConfirm = confirmDialogInfo.action,
            cancelText = "取消",
            confirmColor = Color(0xFF388E3C),
            onDismiss = {
                confirmDialogInfo = confirmDialogInfo.copy(
                    visible = false
                )
            }
        )
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