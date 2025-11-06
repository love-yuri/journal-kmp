package com.yuri.love.views.backup

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.yuri.love.components.DeleteConfirmDialog
import com.yuri.love.components.SimpleTopBar
import com.yuri.love.database.JournalService
import com.yuri.love.database.SystemConfig
import com.yuri.love.share.GlobalStyle
import com.yuri.love.share.GlobalValue
import com.yuri.love.share.NavigatorManager
import com.yuri.love.utils.TimeUtils

private object ThemeConfig {
    val Primary = Color(0xFFFFB3C6)
    val Secondary = Color(0xFFFFC9DE)
    val Background = Color(0xFFFFFBFC)
    val Surface = Color(0xFFFFFFFF)
    val TextPrimary = Color(0xFF4A4A4A)
    val TextSecondary = Color(0xFF9E9E9E)
    val BorderLight = Color(0xFFFCE4EC)
    val Warning = Color(0xFFFFB74D)
    val Gradient1 = listOf(Color(0xFFFFE5EE), Color(0xFFFFF0F5))
    val Gradient2 = listOf(Color(0xFFFFB3C6), Color(0xFFFFC9DE))
    val CardGradient = listOf(Color(0xFFFFFFFF), Color(0xFFFFF5F8))
}

class BackupScreen : Screen {
    @Composable
    override fun Content() {
        var backupHistories by remember { mutableStateOf(SystemConfig.journal_backups) }
        var showBackupDialog by remember { mutableStateOf(false) }
        var showRestoreDialog by remember { mutableStateOf(false) }
        var showWebdavLoginDialog by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ThemeConfig.Background)
                .statusBarsPadding()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                SimpleTopBar()

                BackupScreenContent (
                    onBackupClick = { showBackupDialog = true },
                    onRestoreClick = { showRestoreDialog = true },
                    isLoading = isLoading,
                    backupHistories = backupHistories,
                    onDelete = {
                        val list = backupHistories.toMutableList()
                        it.forEach { k ->
                            list.remove(k)
                        }
                        backupHistories = list
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = showBackupDialog,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            BackupDialog(
                onDismiss = { showBackupDialog = false },
                onConfirm = { backupType ->
                    val needsWebdav = backupType == BackupType.WEBDAV || backupType == BackupType.BOTH
                    if (needsWebdav && !SystemConfig.isLoggedIn) {
                        showBackupDialog = false
                        showWebdavLoginDialog = true
                        return@BackupDialog
                    }

                    showBackupDialog = false
                    isLoading = true

                    when(backupType) {
                        BackupType.LOCAL -> JournalService.localBackup()
                        BackupType.WEBDAV -> JournalService.webdavBackup()
                        BackupType.BOTH -> JournalService.backupAll()
                    }

                    backupHistories = SystemConfig.journal_backups

                    isLoading = false
                }
            )
        }

        AnimatedVisibility(
            visible = showRestoreDialog,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            RestoreDialog {
                 showRestoreDialog = false
            }
        }

        AnimatedVisibility(
            visible = showWebdavLoginDialog,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            WebdavDialog(
                onDismiss = { showWebdavLoginDialog = false },
                onNavigateToSettings = {
                    showWebdavLoginDialog = false
                    GlobalValue.navigatorManager.push(NavigatorManager.ScreenPageType.Webdav)
                }
            )
        }
    }
}

enum class BackupType {
    LOCAL, WEBDAV, BOTH
}

@Composable
fun BackupScreenContent(
    onBackupClick: () -> Unit,
    onRestoreClick: () -> Unit,
    isLoading: Boolean,
    backupHistories: List<JournalService.JournalBackupInfo>,
    onDelete: (List<JournalService.JournalBackupInfo>) -> Unit
) {
    var selectedDeleteItem by remember { mutableStateOf<List<JournalService.JournalBackupInfo>>(listOf()) }
    val isWebdavLoggedIn = SystemConfig.isLoggedIn
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 12.dp, end = 12.dp, top = 12.dp)
    ) {
        Text(
            text = "数据管理中心",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = ThemeConfig.TextPrimary,
            letterSpacing = (-0.5).sp
        )

        Spacer(modifier = Modifier.height(12.dp))
        AnimatedVisibility(
            visible = !isWebdavLoggedIn,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .shadow(4.dp, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFFF9E6),
                                Color(0xFFFFFCF0)
                            )
                        )
                    ).clickable(
                        onClick = {
                            GlobalValue.navigatorManager.push(NavigatorManager.ScreenPageType.Webdav)
                        },
                        indication = GlobalStyle.ripple,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CloudOff,
                            contentDescription = null,
                            tint = ThemeConfig.Warning,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "WebDAV 未连接",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ThemeConfig.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "需要先登录才能使用云端备份",
                            fontSize = 13.sp,
                            color = ThemeConfig.TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = ThemeConfig.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        ActionBtn("备份数据", Icons.Outlined.CloudUpload, onBackupClick)
        Spacer(modifier = Modifier.height(14.dp))
        ActionBtn("恢复数据",  Icons.Outlined.CloudDownload, onRestoreClick)

        // 加载进度条
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(ThemeConfig.BorderLight)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(ThemeConfig.Gradient2)
                            )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "正在处理中...",
                    fontSize = 12.sp,
                    color = ThemeConfig.TextSecondary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        // 备份历史
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "备份记录",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ThemeConfig.TextPrimary,
                letterSpacing = (-0.3).sp
            )

            DeleteIcon {
                selectedDeleteItem = backupHistories
            }

            Text(
                text = "${backupHistories.size} 条",
                fontSize = 13.sp,
                color = ThemeConfig.TextSecondary,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ThemeConfig.BorderLight)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        if (backupHistories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.Inventory2,
                        contentDescription = null,
                        tint = ThemeConfig.TextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "还没有备份记录",
                        fontSize = 15.sp,
                        color = ThemeConfig.TextSecondary
                    )
                }
            }
        } else {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                val message = when(selectedDeleteItem.size) {
                    0 -> "无效操作!"
                    1 -> "确定要删除 ${selectedDeleteItem.first().title}: ${selectedDeleteItem.first().name} 吗？"
                    else -> "确定要删除 ${selectedDeleteItem.size} 项吗？"
                }
                DeleteConfirmDialog(
                    visible = selectedDeleteItem.isNotEmpty(),
                    title = "确认删除",
                    message = message,
                    onConfirm = {
                        selectedDeleteItem.forEach { backup ->
                            JournalService.deleteBackup(backup)
                        }
                        onDelete(selectedDeleteItem)
                        selectedDeleteItem = listOf()
                    },
                    onDismiss = {
                        selectedDeleteItem = listOf()
                    }
                )

                backupHistories.forEach { backup ->
                    Spacer(modifier = Modifier.height(6.dp))
                    BackupHistoryItem(
                        icon = backup.icon,
                        title = backup.title,
                        date = TimeUtils.formatTimestamp(backup.date)
                    ) {
                        selectedDeleteItem = listOf(backup)
                    }
                }
            }
        }
    }
}

@Composable
fun BackupHistoryItem(
    icon: ImageVector,
    title: String,
    date: String,
    onDelete: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(ThemeConfig.CardGradient)
            )
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(ThemeConfig.Gradient1)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ThemeConfig.Primary,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ThemeConfig.TextPrimary,
                    letterSpacing = (-0.2).sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = ThemeConfig.TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = date,
                        fontSize = 13.sp,
                        color = ThemeConfig.TextSecondary
                    )
                }
            }

            DeleteIcon(onDelete)
        }
    }

}

/**
 * 删除图标组件
 * @param onClick 点击删除图标时的回调函数
 */
@Composable
private fun DeleteIcon(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ThemeConfig.Background)
            .border(1.dp, ThemeConfig.Secondary, RoundedCornerShape(8.dp))
            .clickable(
                onClick = onClick,
                indication = GlobalStyle.ripple,
                interactionSource = remember { MutableInteractionSource() }
            ).padding(4.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = null,
            tint = ThemeConfig.Primary,
            modifier = Modifier.size(25.dp)
        )
    }
}

@Composable
private fun ActionBtn (
    title: String,
    icon: ImageVector,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ThemeConfig.Background)
            .border(1.dp, ThemeConfig.Secondary, RoundedCornerShape(12.dp))
            .clickable(
                onClick = onClick,
                indication = GlobalStyle.ripple,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(ThemeConfig.Gradient1)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ThemeConfig.Primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 23.sp,
                fontWeight = FontWeight.SemiBold,
                color = ThemeConfig.TextSecondary,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = ThemeConfig.TextSecondary,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun BackupDialog(
    onDismiss: () -> Unit,
    onConfirm: (BackupType) -> Unit
) {
    var selectedType by remember { mutableStateOf(BackupType.BOTH) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                onClick = onDismiss,
                indication = GlobalStyle.ripple,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .shadow(20.dp, RoundedCornerShape(28.dp))
                .clip(RoundedCornerShape(28.dp))
                .background(ThemeConfig.Surface)
                .clickable(
                    enabled = false,
                    indication = GlobalStyle.ripple,
                    interactionSource = remember { MutableInteractionSource() }
                ) {}
                .padding(26.dp)
        ) {
            Column {
                // 标题区域
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(ThemeConfig.Gradient2)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.CloudUpload,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "选择备份方式",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = ThemeConfig.TextPrimary,
                            letterSpacing = (-0.3).sp
                        )
                        Text(
                            text = "选择合适的备份位置",
                            fontSize = 13.sp,
                            color = ThemeConfig.TextSecondary
                        )
                    }
                }

                // 选项
                BackupOption(
                    icon = Icons.Outlined.Folder,
                    title = "本地备份",
                    subtitle = "保存在设备存储空间",
                    selected = selectedType == BackupType.LOCAL,
                    onClick = { selectedType = BackupType.LOCAL }
                )

                Spacer(modifier = Modifier.height(12.dp))

                BackupOption(
                    icon = Icons.Outlined.Cloud,
                    title = "WebDAV 备份",
                    subtitle = "同步到云端服务器",
                    selected = selectedType == BackupType.WEBDAV,
                    onClick = { selectedType = BackupType.WEBDAV }
                )

                Spacer(modifier = Modifier.height(12.dp))

                BackupOption(
                    icon = Icons.Outlined.DoneAll,
                    title = "双重备份",
                    subtitle = "本地 + 云端同时保存",
                    selected = selectedType == BackupType.BOTH,
                    onClick = { selectedType = BackupType.BOTH }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ClickableButton (
                        text = "取消",
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss,
                        background = ThemeConfig.Gradient1
                    )

                    ClickableButton (
                        text = "开始备份",
                        modifier = Modifier.weight(1f),
                        onClick = { onConfirm(selectedType) },
                        background = ThemeConfig.Gradient2
                    )
                }
            }
        }
    }
}

@Composable
private fun ClickableButton(
    modifier: Modifier,
    text: String,
    onClick: () -> Unit,
    background: List<Color>
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(background))
            .clickable(
                onClick = onClick,
                indication = GlobalStyle.ripple,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = ThemeConfig.TextSecondary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp
        )
    }
}

@Composable
private fun RestoreDialog(onDismiss: () -> Unit) {
    val allBackupInfo = SystemConfig.journal_backups
    var selectedIndex by remember { mutableIntStateOf(-1) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                onClick = onDismiss,
                indication = GlobalStyle.ripple,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .shadow(20.dp, RoundedCornerShape(28.dp))
                .clip(RoundedCornerShape(28.dp))
                .background(ThemeConfig.Surface)
                .clickable(
                    enabled = false,
                    indication = GlobalStyle.ripple,
                    interactionSource = remember { MutableInteractionSource() }
                ) {}
                .padding(26.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(0.8f)
            ) {
                // 标题区域
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(ThemeConfig.Gradient2)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.CloudDownload,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "恢复数据",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = ThemeConfig.TextPrimary,
                            letterSpacing = (-0.3).sp
                        )
                        Text(
                            text = "选择要恢复的备份",
                            fontSize = 13.sp,
                            color = ThemeConfig.TextSecondary
                        )
                    }
                }

                Column (modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                ) {
                    // 备份列表
                    allBackupInfo.forEachIndexed { index, it ->
                        BackupOption(
                            icon = it.icon,
                            title = it.name,
                            subtitle = TimeUtils.formatTimestamp(it.date),
                            selected = index == selectedIndex,
                            onClick = { selectedIndex = index }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // 警告提示
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFFF3E0))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = null,
                            tint = ThemeConfig.Warning,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "恢复操作会覆盖当前所有数据，请谨慎选择",
                            fontSize = 13.sp,
                            color = Color(0xFFE65100),
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 按钮
                    ClickableButton (
                        text = "取消",
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss,
                        background = ThemeConfig.Gradient1
                    )

                    ClickableButton (
                        text = "确认恢复",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (selectedIndex != -1) {
                                JournalService.restore(allBackupInfo[selectedIndex])
                            }
                            onDismiss()
                        },
                        background = listOf(ThemeConfig.Warning, ThemeConfig.Warning)
                    )
                }
            }
        }
    }
}

@Composable
private fun WebdavDialog(
    onDismiss: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                onClick = onDismiss,
                indication = GlobalStyle.ripple,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .shadow(20.dp, RoundedCornerShape(28.dp))
                .clip(RoundedCornerShape(28.dp))
                .background(ThemeConfig.Surface)
                .clickable(
                    enabled = false,
                    indication = GlobalStyle.ripple,
                    interactionSource = remember { MutableInteractionSource() }
                ) {}
                .padding(26.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF3E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.CloudOff,
                        contentDescription = null,
                        tint = ThemeConfig.Warning,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "需要登录 WebDAV",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ThemeConfig.TextPrimary,
                    letterSpacing = (-0.3).sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "使用云端备份功能需要先配置 WebDAV 连接。前往设置页面完成登录？",
                    fontSize = 15.sp,
                    color = ThemeConfig.TextSecondary,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .shadow(2.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(ThemeConfig.BorderLight)
                            .clickable(
                                onClick = onDismiss,
                                indication = GlobalStyle.ripple,
                                interactionSource = remember { MutableInteractionSource() }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "取消",
                            color = ThemeConfig.TextSecondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .shadow(6.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(ThemeConfig.Gradient2)
                            )
                            .clickable(
                                onClick = onNavigateToSettings,
                                indication = GlobalStyle.ripple,
                                interactionSource = remember { MutableInteractionSource() }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "前往设置",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BackupOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (selected) 6.dp else 2.dp,
                shape = RoundedCornerShape(14.dp)
            )
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (selected) {
                    Brush.horizontalGradient(ThemeConfig.Gradient1)
                } else {
                    Brush.horizontalGradient(
                        listOf(ThemeConfig.Surface, ThemeConfig.Surface)
                    )
                }
            )
            .clickable(
                onClick = onClick,
                indication = GlobalStyle.ripple,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (selected) {
                            Brush.linearGradient(ThemeConfig.Gradient2)
                        } else {
                            Brush.linearGradient(ThemeConfig.Gradient1)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) Color.White else ThemeConfig.Primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selected) ThemeConfig.TextPrimary else ThemeConfig.TextPrimary,
                    letterSpacing = (-0.2).sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = ThemeConfig.TextSecondary
                )
            }
        }
    }
}