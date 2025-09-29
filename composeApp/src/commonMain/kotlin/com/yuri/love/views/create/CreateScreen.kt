package com.yuri.love.views.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.yuri.love.Journal
import com.yuri.love.database.JournalService
import com.yuri.love.share.GlobalValue
import com.yuri.love.utils.TimeUtils
import com.yuri.love.utils.TimeUtils.nowTime
import com.yuri.love.utils.algorithm.SnowFlake
import com.yuri.love.utils.notification.Notification
import com.yuri.love.utils.platformSafeTopPadding
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime

private val primaryColor = Color(0xFFFFB6C1)
private val backgroundColor = Color(0xFFFFFFFF)
private val textPrimary = Color(0xFF111827)
private val textSecondary = Color(0xFF6B7280)
private val textTertiary = Color(0xFF9CA3AF)
private val dividerColor = Color(0xFFE5E7EB)

class CreateScreen(val journal: Journal? = null): Screen {
    private val isUpdate get() = journal != null

    @Preview
    @Composable
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
    override fun Content() {
        val navigator = LocalNavigator.current

        var title by remember { mutableStateOf(journal?.title ?: "")  }
        var content by remember { mutableStateOf(journal?.content ?: "") }
        val scrollState = rememberScrollState()
        val currentDate = remember {
            val localDate = nowTime.toLocalDateTime(TimeZone.currentSystemDefault()).date
            "${localDate.month.number}/${localDate.day}"
        }

        val imeInsets = WindowInsets.ime
        val imeBottom = with(LocalDensity.current) {
            imeInsets.getBottom(this).toDp()
        }

        Column(
            modifier = Modifier
                .padding(bottom = imeBottom)
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            // 顶部导航 - 极简设计
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .platformSafeTopPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navigator?.pop() },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = textPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = currentDate,
                            fontSize = 15.sp,
                            color = textSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = GlobalValue.weather,
                            fontSize = 15.sp,
                            color = textSecondary
                        )
                    }

                    IconButton(
                        onClick = {
                            try {
                                if (isUpdate) {
                                    updateJournal(journal, title, content)
                                } else {
                                    addJournal(title, content)
                                }
                                navigator?.pop()
                            } catch (e: Exception) {
                                Notification.notificationState?.error("操作失败 -> ${e.message}")
                            }
                        },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // 分割线
                HorizontalDivider(
                    color = dividerColor,
                    thickness = 1.5.dp
                )
            }

            // 主内容区域
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                // 标题输入
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(50.dp)
                        .padding(horizontal = 5.dp)
                ) {
                    BasicTextField(
                        value = title,
                        modifier = Modifier.align(Alignment.CenterStart),
                        onValueChange = { title = it },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            lineHeight = 32.sp
                        ),
                        cursorBrush = SolidColor(primaryColor),
                        decorationBox = { innerTextField ->
                            if (title.isEmpty()) {
                                Text(
                                    text = "标题",
                                    fontSize = 28.sp,
                                    color = textTertiary,
                                    fontWeight = FontWeight.Normal,
                                    lineHeight = 32.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                            innerTextField()
                        }
                    )
                }


                // 标题下分割线
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(primaryColor)
                )

                Spacer(modifier = Modifier.height(3.dp))

                // 内容编辑区域
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.Transparent)
                ) {
                    LaunchedEffect(content) {
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }

                    BasicTextField(
                        value = content,
                        onValueChange = { content = it },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = textPrimary,
                            fontWeight = FontWeight.Normal
                        ),
                        cursorBrush = SolidColor(primaryColor),
                        modifier = Modifier.fillMaxSize(),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 8.dp),
                            ) {
                                if (content.isEmpty()) {
                                    Text(
                                        text = "写点什么...",
                                        color = textTertiary,
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            color = textPrimary,
                                            fontWeight = FontWeight.Normal,
                                        ),
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }

            // 底部状态栏
            ToolbarWithIme(content)
        }
    }
}

@Composable
fun ToolbarWithIme(content: String) {
    Column {
        HorizontalDivider(
            color = dividerColor,
            thickness = 0.5.dp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${content.length} 字",
                fontSize = 14.sp,
                color = textSecondary,
                fontWeight = FontWeight.Medium
            )

            if (content.isNotEmpty()) {
                Text(
                    text = when {
                        content.length < 50 -> "继续..."
                        content.length < 200 -> "不错"
                        else -> "很棒"
                    },
                    fontSize = 14.sp,
                    color = when {
                        content.length < 50 -> Color(0xFFF59E0B)
                        content.length < 200 -> primaryColor
                        else -> Color(0xFF10B981)
                    },
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun addJournal(title: String?, content: String) {
    val journal = Journal(
        id = SnowFlake.nextId(),
        title = title?.ifEmpty { null },
        content = content,
        createdAt = TimeUtils.now,
        updatedAt = TimeUtils.now,
        mood = "",
        weather = GlobalValue.weather
    )
    if (JournalService.insert(journal)) {
        Notification.notificationState?.success("日记已保存!")
        return
    }
    throw RuntimeException("保存失败!")
}

private fun updateJournal(journal: Journal?, title: String?, content: String) {
    journal ?: throw RuntimeException("journal is null, update error!")
    val res = JournalService.update (journal.copy(
        title = title?.ifEmpty { null },
        content = content,
    ))
    if (res) {
        Notification.notificationState?.success("更新成功")
        return
    }

    throw RuntimeException("更新失败!")
}