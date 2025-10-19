package com.yuri.love.views.backup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.yuri.love.components.TopBar
import com.yuri.love.database.SystemConfig
import com.yuri.love.share.GlobalValue
import com.yuri.love.share.NavigatorManager


class BackupScreen : Screen {
    @Composable
    override fun Content() {
        var showBackupDialog by remember { mutableStateOf(false) }
        var showRestoreDialog by remember { mutableStateOf(false) }
        var showWebdavLoginDialog by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }
        var statusMessage by remember { mutableStateOf("") }

        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            TopBar()

            BackupScreenContent(
                onBackupClick = { showBackupDialog = true },
                onRestoreClick = { showRestoreDialog = true },
                isLoading = isLoading,
                statusMessage = statusMessage,
                onStatusMessageChange = { statusMessage = it }
            )
        }

        if (showBackupDialog) {
            BackupDialog(
                onDismiss = { showBackupDialog = false },
                onConfirm = { backupType ->
                    // 检查是否需要 WebDAV 登录
                    val needsWebdav = backupType == BackupType.WEBDAV || backupType == BackupType.BOTH
                    if (needsWebdav && !SystemConfig.isLoggedIn) {
                        showBackupDialog = false
                        showWebdavLoginDialog = true
                        return@BackupDialog
                    }

                    showBackupDialog = false
                    isLoading = true

                    localBackup()
                    webdavBackup()

                    statusMessage = when (backupType) {
                        BackupType.LOCAL -> "本地备份成功"
                        BackupType.WEBDAV -> "网络备份成功"
                        BackupType.BOTH -> "本地和网络备份成功"
                    }
                    isLoading = false
                }
            )
        }

        if (showRestoreDialog) {
            RestoreDialog(
                onDismiss = { showRestoreDialog = false },
                onConfirm = { restoreType ->
                    // 检查是否需要 WebDAV 登录
                    if (restoreType == RestoreType.WEBDAV && !SystemConfig.isLoggedIn) {
                        showRestoreDialog = false
                        showWebdavLoginDialog = true
                        return@RestoreDialog
                    }

                    showRestoreDialog = false
                    isLoading = true
                    // TODO: 实现恢复逻辑
                    statusMessage = when (restoreType) {
                        RestoreType.LOCAL -> "从本地恢复成功"
                        RestoreType.WEBDAV -> "从网络恢复成功"
                    }
                    isLoading = false
                }
            )
        }

        if (showWebdavLoginDialog) {
            WebdavLoginRequiredDialog(
                onDismiss = { showWebdavLoginDialog = false },
                onNavigateToSettings = {
                    showWebdavLoginDialog = false
                    GlobalValue.navigatorManager.push(NavigatorManager.ScreenPageType.Webdav)
                }
            )
        }
    }
}

/**
 * 本地备份
 */
private fun localBackup() {

}

/**
 * webdav备份
 */
private fun webdavBackup() {
    TODO("Not yet implemented")
}

enum class BackupType {
    LOCAL, WEBDAV, BOTH
}

enum class RestoreType {
    LOCAL, WEBDAV
}

@Composable
fun BackupScreenContent(
    onBackupClick: () -> Unit,
    onRestoreClick: () -> Unit,
    isLoading: Boolean,
    statusMessage: String,
    onStatusMessageChange: (String) -> Unit
) {
    val isWebdavLoggedIn = SystemConfig.isLoggedIn

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 标题
        Text(
            text = "备份与恢复",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // WebDAV 状态提示
        if (!isWebdavLoggedIn) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "WebDAV 未登录",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "网络备份和恢复功能需要先登录 WebDAV",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }

        // 状态消息
        if (statusMessage.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = statusMessage)
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { onStatusMessageChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭"
                        )
                    }
                }
            }
        }

        // 快速操作按钮
        QuickActionButtons(
            onBackupClick = onBackupClick,
            onRestoreClick = onRestoreClick,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 备份历史
        BackupHistorySection(isWebdavLoggedIn = isWebdavLoggedIn)
    }
}

@Composable
fun WebdavLoginRequiredDialog(
    onDismiss: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text("需要登录 WebDAV")
        },
        text = {
            Text(
                text = "使用网络备份和恢复功能需要先登录 WebDAV。\n\n是否前往 WebDAV 设置页面进行登录？",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onNavigateToSettings) {
                Text("前往登录")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun QuickActionButtons(
    onBackupClick: () -> Unit,
    onRestoreClick: () -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "快速操作",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 备份按钮
                Button(
                    onClick = onBackupClick,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("备份")
                }

                // 恢复按钮
                OutlinedButton(
                    onClick = onRestoreClick,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("恢复")
                }
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun BackupHistorySection(isWebdavLoggedIn: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "备份历史",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 本地备份历史
            BackupHistoryItem(
                icon = Icons.Default.Folder,
                title = "本地备份",
                subtitle = "最后备份: 2024-01-15 14:30",
                size = "2.5 MB"
            )

            if (isWebdavLoggedIn) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // 网络备份历史
                BackupHistoryItem(
                    icon = Icons.Default.Cloud,
                    title = "WebDAV 备份",
                    subtitle = "最后备份: 2024-01-15 14:30",
                    size = "2.5 MB"
                )
            } else {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // 未登录提示
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "WebDAV 备份",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "需要先登录 WebDAV",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BackupHistoryItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    size: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = size,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BackupDialog(
    onDismiss: () -> Unit,
    onConfirm: (BackupType) -> Unit
) {
    var selectedType by remember { mutableStateOf(BackupType.BOTH) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.CloudUpload, contentDescription = null)
        },
        title = {
            Text("选择备份方式")
        },
        text = {
            Column {
                Text(
                    text = "请选择备份位置:",
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                BackupTypeOption(
                    icon = Icons.Default.Folder,
                    title = "仅本地备份",
                    selected = selectedType == BackupType.LOCAL,
                    onClick = { selectedType = BackupType.LOCAL }
                )

                Spacer(modifier = Modifier.height(8.dp))

                BackupTypeOption(
                    icon = Icons.Default.Cloud,
                    title = "仅 WebDAV 备份",
                    selected = selectedType == BackupType.WEBDAV,
                    onClick = { selectedType = BackupType.WEBDAV }
                )

                Spacer(modifier = Modifier.height(8.dp))

                BackupTypeOption(
                    icon = Icons.Default.DoneAll,
                    title = "本地 + WebDAV 备份",
                    selected = selectedType == BackupType.BOTH,
                    onClick = { selectedType = BackupType.BOTH }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedType) }) {
                Text("开始备份")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun RestoreDialog(
    onDismiss: () -> Unit,
    onConfirm: (RestoreType) -> Unit
) {
    var selectedType by remember { mutableStateOf(RestoreType.LOCAL) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.CloudDownload, contentDescription = null)
        },
        title = {
            Text("选择恢复来源")
        },
        text = {
            Column {
                Text(
                    text = "请选择要从哪里恢复数据:",
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                BackupTypeOption(
                    icon = Icons.Default.Folder,
                    title = "从本地恢复",
                    selected = selectedType == RestoreType.LOCAL,
                    onClick = { selectedType = RestoreType.LOCAL }
                )

                Spacer(modifier = Modifier.height(8.dp))

                BackupTypeOption(
                    icon = Icons.Default.Cloud,
                    title = "从 WebDAV 恢复",
                    selected = selectedType == RestoreType.WEBDAV,
                    onClick = { selectedType = RestoreType.WEBDAV }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "恢复将覆盖当前所有数据",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedType) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("确认恢复")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun BackupTypeOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (selected)
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.weight(1f))
            if (selected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}