package com.yuri.love.views.webdav

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import com.yuri.love.components.ModernIconButton
import com.yuri.love.database.SystemConfig
import com.yuri.love.retrofit.WebDavService
import com.yuri.love.retrofit.WebDavService.WebdavFile
import com.yuri.love.share.GlobalColors
import com.yuri.love.share.GlobalValue
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLDecoder

data class WebdavConfig(
    var serverUrl: String = "",
    var username: String = "",
    var password: String = "",
    var isLoggedIn: Boolean = false
)

class WebdavScreen : Screen {
    @Composable
    override fun Content() {
        ModernWebdavScreen()
    }
}

// 现代配色方案
object ModernColors {
    val Primary = Color(0xFFFFB3C6)          // 柔和的粉色
    val Secondary = Color(0xFFFFC9DE)        // 淡粉色
    val Background = Color(0xFFFFFBFC)       // 极浅的粉白色
    val Surface = Color(0xFFFFFFFF)          // 纯白
    val TextPrimary = Color(0xFF4A4A4A)      // 柔和的深灰色
    val TextSecondary = Color(0xFF9E9E9E)    // 淡灰色
    val BorderLight = Color(0xFFFCE4EC)      // 粉白边框
    val Success = Color(0xFF49DCC0)
    val Gradient1 = listOf(
        Color(0xFFFFE5EE),                    // 极淡粉
        Color(0xFFFFF0F5)                     // 淡粉白
    )
}

@OptIn(ExperimentalMaterial3Api::class, InternalVoyagerApi::class)
@Composable
fun ModernWebdavScreen() {
    var webdavConfig by remember {
        mutableStateOf(WebdavConfig (
            serverUrl = WebDavService.HOST,
            username = SystemConfig.webdav_account ?: "",
            password = SystemConfig.webdav_password ?: "",
            isLoggedIn = SystemConfig.isLoggedIn
        ))
    }

    var currentFiles by remember { mutableStateOf<List<WebdavFile>>(emptyList()) }
    var currentPath by remember { mutableStateOf(WebDavService.DEFAULT_PATH) }
    var showLoginDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (webdavConfig.isLoggedIn) {
            currentFiles = WebDavService.dir(WebDavService.DEFAULT_PATH).drop(1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ModernColors.Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ModernTopBar(
                isLoggedIn = webdavConfig.isLoggedIn,
                onLoginClick = { showLoginDialog = true },
                onRefreshClick = {
                    scope.launch {
                        isLoading = true
                        currentFiles = WebDavService.dir(currentPath).drop(1)
                        isLoading = false
                    }
                },
                onBackClick = {
                    GlobalValue.navigatorManager.pop()
                }
            )

            if (!webdavConfig.isLoggedIn) {
                // 现代化空状态
                ModernEmptyState {
                    showLoginDialog = true
                }
            } else {
                // 文件内容区域
                Column(modifier = Modifier.fillMaxSize()) {
                    // 路径导航
                    ModernPathBar(
                        currentPath = currentPath,
                        onPathClick = { path ->
                            currentPath = path
                            scope.launch {
                                isLoading = true
                                currentFiles = WebDavService.dir(path).drop(1)
                                isLoading = false
                            }
                        }
                    )

                    if (isLoading) {
                        ModernLoadingView()
                    } else {
                        ModernFileList(
                            files = currentFiles,
                            onFileClick = { file ->
                                if (file.isFolder) {
                                    currentPath = file.path
                                    scope.launch {
                                        isLoading = true
                                        currentFiles = WebDavService.dir(currentPath).drop(1)
                                        isLoading = false
                                    }
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("打开文件: ${file.fileName}")
                                    }
                                }
                            },
                            onFileDownload = { file ->
                                scope.launch {
                                    snackbarHostState.showSnackbar("下载: ${file.fileName}")
                                }
                            }
                        )
                    }
                }
            }
        }

        if (showLoginDialog) {
            ModernLoginDialog(
                config = webdavConfig,
                onDismiss = { showLoginDialog = false },
                onLogin = { url, username, password ->
                    scope.launch {
                        isLoading = true

                        try {
                            SystemConfig.webdav_account = username
                            SystemConfig.webdav_password = password
                            currentFiles = WebDavService.dir(WebDavService.DEFAULT_PATH).drop(1)
                            webdavConfig = WebdavConfig (
                                serverUrl = url,
                                username = username,
                                password = password,
                                isLoggedIn = true
                            )
                            showLoginDialog = false
                            isLoading = false
                            SystemConfig.isLoggedIn = true
                            snackbarHostState.showSnackbar("✨ 登录成功")
                        } catch (e: Exception) {
                            showLoginDialog = false
                            isLoading = false
                            snackbarHostState.showSnackbar("❌ 登录失败 ${e.message}")
                        }
                    }
                },
                onLogout = {
                    webdavConfig.isLoggedIn = false
                    currentFiles = emptyList()
                    currentPath = "/"
                    showLoginDialog = false
                    SystemConfig.isLoggedIn = false
                    scope.launch {
                        snackbarHostState.showSnackbar("👋 已退出登录")
                    }
                }
            )
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
fun ModernTopBar(
    isLoggedIn: Boolean,
    onLoginClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onBackClick: (() -> Unit)? = null, // 可选的返回按钮
    title: String = "WebDAV",
    subtitle: String? = null // 可选的自定义副标题
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(ModernColors.Gradient1)
            )
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧：返回按钮 + 标题
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 返回按钮
                if (onBackClick != null) {
                    ModernIconButton(
                        icon = Icons.AutoMirrored.Outlined.ArrowBack,
                        onClick = onBackClick,
                        contentDescription = "返回",
                        tint = Color(0xFFEC4899)
                    )
                }

                // 标题区域
                Row (
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEC4899),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // 状态或副标题
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically).padding(start = 8.dp),
                        text = subtitle ?: if (isLoggedIn) "已连接 ✓" else "未连接",
                        fontSize = 13.sp,
                        color = Color(0xFF66A84D),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 右侧：操作按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLoggedIn) {
                    ModernIconButton(
                        icon = Icons.Outlined.Refresh,
                        onClick = onRefreshClick,
                        contentDescription = "刷新",
                        tint = Color(0xFFEC4899)
                    )
                }

                ModernIconButton(
                    icon = if (isLoggedIn) Icons.Outlined.Settings else Icons.AutoMirrored.Outlined.Login,
                    onClick = onLoginClick,
                    contentDescription = if (isLoggedIn) "设置" else "登录",
                    tint = Color(0xFFEC4899)
                )
            }
        }
    }
}

@Composable
fun ModernEmptyState(onLoginClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(40.dp)
        ) {
            // 渐变图标背景
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(ModernColors.Gradient1)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CloudOff,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "还未连接到服务器",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ModernColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "配置 WebDAV 服务器开始使用",
                fontSize = 15.sp,
                color = ModernColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 现代化按钮
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .height(56.dp)
                    .widthIn(min = 200.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(ModernColors.Gradient1)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Outlined.Login,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Text(
                            "立即连接",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernPathBar(
    currentPath: String,
    onPathClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Folder,
            contentDescription = null,
            tint = ModernColors.Primary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        val pathParts = currentPath.split("/").filter { it.isNotEmpty() }

        Text(
            text = "根目录",
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onPathClick("/") }
                .padding(horizontal = 8.dp, vertical = 4.dp),
            color = ModernColors.Primary,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )

        pathParts.forEachIndexed { index, part ->
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = ModernColors.TextSecondary
            )
            Text(
                text = decodePathComponents(part),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        val path = "/" + pathParts.subList(0, index + 1).joinToString("/")
                        onPathClick(path)
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = if (index == pathParts.lastIndex)
                    ModernColors.TextPrimary
                else
                    ModernColors.Primary,
                fontWeight = if (index == pathParts.lastIndex) FontWeight.SemiBold else FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

/**
 * 获取路径的各个部分 解码中文
 */
private fun decodePathComponents(path: String): String {
    return path.split("/").joinToString("/") { component ->
        URLDecoder.decode(component, "UTF-8")
    }
}

@Composable
fun ModernLoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = ModernColors.Primary,
                strokeWidth = 4.dp
            )
            Text(
                text = "加载中...",
                color = ModernColors.TextSecondary,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ModernFileList(
    files: List<WebdavFile>,
    onFileClick: (WebdavFile) -> Unit,
    onFileDownload: (WebdavFile) -> Unit
) {
    if (files.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Outlined.FolderOpen,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = ModernColors.TextSecondary.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "此文件夹为空",
                    color = ModernColors.TextSecondary,
                    fontSize = 15.sp
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(files) { file ->
                ModernFileItem(
                    file = file,
                    onClick = { onFileClick(file) },
                    onDownload = { onFileDownload(file) }
                )
            }
        }
    }
}

@Composable
fun ModernFileItem(
    file: WebdavFile,
    onClick: () -> Unit,
    onDownload: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isPressed) 8.dp else 2.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = ModernColors.Primary.copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(ModernColors.Surface)
            .clickable {
                isPressed = true
                onClick()
            }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 文件图标
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (file.isFolder)
                            ModernColors.Primary.copy(alpha = 0.1f)
                        else
                            ModernColors.Secondary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (file.isFolder) Icons.Outlined.Folder else getFileIcon(file.fileName),
                    contentDescription = null,
                    tint = if (file.isFolder) ModernColors.Primary else ModernColors.Secondary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 文件信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.fileName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ModernColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (file.isFolder) "文件夹" else formatFileSize(null),
                        fontSize = 13.sp,
                        color = ModernColors.TextSecondary
                    )
                }
            }

            // 操作按钮
            if (!file.isFolder) {
                IconButton(
                    onClick = onDownload,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ModernColors.Primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Download,
                            contentDescription = "下载",
                            tint = ModernColors.Primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else {
                Icon(
                    Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = ModernColors.TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernLoginDialog(
    config: WebdavConfig,
    onDismiss: () -> Unit,
    onLogin: (String, String, String) -> Unit,
    onLogout: () -> Unit
) {
    var serverUrl by remember { mutableStateOf(config.serverUrl) }
    var username by remember { mutableStateOf(config.username) }
    var password by remember { mutableStateOf(config.password) }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp))
                .background(ModernColors.Surface)
                .clickable(enabled = false) { }
                .padding(24.dp)
        ) {
            Column {
                // 标题
                Text(
                    text = if (config.isLoggedIn) "服务器设置" else "连接到 WebDAV",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ModernColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (config.isLoggedIn) {
                    // 已连接状态
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        ModernColors.Success.copy(alpha = 0.1f),
                                        ModernColors.Success.copy(alpha = 0.05f)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(ModernColors.Success.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = ModernColors.Success,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    "已连接",
                                    fontWeight = FontWeight.Bold,
                                    color = ModernColors.Success,
                                    fontSize = 16.sp
                                )
                                Text(
                                    config.username,
                                    fontSize = 13.sp,
                                    color = ModernColors.TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 退出按钮
                    OutlinedButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFE74C3C)
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("断开连接", fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    // 登录表单
                    ModernTextField(
                        value = serverUrl,
                        onValueChange = { serverUrl = it },
                        label = "服务器地址",
                        placeholder = WebDavService.HOST,
                        leadingIcon = Icons.Outlined.Cloud
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ModernTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = "用户名",
                        placeholder = "输入用户名",
                        leadingIcon = Icons.Outlined.Person
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ModernTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "密码",
                        placeholder = "输入密码",
                        leadingIcon = Icons.Outlined.Lock,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onPasswordVisibilityToggle = { passwordVisible = !passwordVisible }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 登录按钮
                    Button(
                        onClick = { onLogin(serverUrl, username, password) },
                        enabled = serverUrl.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = ModernColors.BorderLight
                        ),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    if (serverUrl.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty())
                                        Brush.horizontalGradient(ModernColors.Gradient1)
                                    else
                                        Brush.horizontalGradient(listOf(ModernColors.BorderLight, ModernColors.BorderLight))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "连接",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 取消按钮
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("取消", color = ModernColors.TextSecondary)
                }
            }
        }
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityToggle: (() -> Unit)? = null
) {
    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = ModernColors.TextSecondary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        OutlinedTextField (
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = ModernColors.TextSecondary.copy(alpha = 0.5f)) },
            leadingIcon = {
                Icon(
                    leadingIcon,
                    contentDescription = null,
                    tint = ModernColors.Primary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = if (isPassword && onPasswordVisibilityToggle != null) {
                {
                    IconButton(onClick = onPasswordVisibilityToggle) {
                        Icon(
                            if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = null,
                            tint = ModernColors.TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible)
                androidx.compose.ui.text.input.PasswordVisualTransformation()
            else
                androidx.compose.ui.text.input.VisualTransformation.None,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ModernColors.Primary,
                unfocusedBorderColor = ModernColors.BorderLight,
                focusedTextColor = ModernColors.TextPrimary,
                unfocusedTextColor = ModernColors.TextPrimary
            ),
            singleLine = true
        )
    }
}

// 辅助函数
fun getFileIcon(fileName: String?): ImageVector {
    return when (fileName?.substringAfterLast(".", "")?.lowercase()) {
        "pdf" -> Icons.Outlined.PictureAsPdf
        "jpg", "jpeg", "png", "gif", "webp" -> Icons.Outlined.Image
        "mp4", "avi", "mkv" -> Icons.Outlined.VideoFile
        "mp3", "wav", "flac" -> Icons.Outlined.AudioFile
        "zip", "rar", "7z" -> Icons.Outlined.FolderZip
        "txt", "md" -> Icons.Outlined.Description
        else -> Icons.AutoMirrored.Outlined.InsertDriveFile
    }
}

fun formatFileSize(size: Long?): String {
    if (size == null) return "未知大小"
    return when {
        size < 1024 -> "$size B"
        size < 1024 * 1024 -> "${size / 1024} KB"
        size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
        else -> "${size / (1024 * 1024 * 1024)} GB"
    }
}

private suspend fun loadDirectory(files: List<WebdavFile>): WebdavFile {
    if (!files.isNotEmpty() || !files[0].isFolder) {
        throw Exception("文件列表为空, 或者第一个文件不是文件夹")
    }
    val file = files[0]
    file.children = mutableListOf()
    for(i in 1..<files.size) {
        val child = files[i]
        if (child.isFile) {
            file.children!!.add(child)
        } else {
            file.children!!.add(loadDirectory(WebDavService.dir(child.path)))
        }
    }

    return file
}