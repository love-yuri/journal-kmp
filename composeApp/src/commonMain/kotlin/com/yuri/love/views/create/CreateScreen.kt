package com.yuri.love.views.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
            // 顶部导航
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .platformSafeTopPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navigator?.pop() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = textPrimary.copy(alpha = 0.05f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .background(
                                color = primaryColor.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = currentDate,
                            fontSize = 13.sp,
                            color = textSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .background(textSecondary.copy(alpha = 0.3f), CircleShape)
                        )
                        Text(
                            text = GlobalValue.weather,
                            fontSize = 13.sp,
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
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = primaryColor.copy(alpha = 0.12f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // 分割线
                HorizontalDivider(
                    color = dividerColor.copy(alpha = 0.5f),
                    thickness = 0.5.dp
                )
            }

            // 主内容区域
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // 标题输入区域 - 固定高度
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    BasicTextField(
                        value = title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterStart),
                        onValueChange = { title = it },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        ),
                        cursorBrush = SolidColor(primaryColor),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxWidth()) {
                                if (title.isEmpty()) {
                                    Text(
                                        text = "标题",
                                        fontSize = 26.sp,
                                        color = textTertiary,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 装饰性分割线
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(primaryColor, shape = CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.5.dp)
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    colors = listOf(
                                        primaryColor.copy(alpha = 0.6f),
                                        primaryColor.copy(alpha = 0.1f)
                                    )
                                )
                            )
                    )
                }

                // 内容编辑区域
                LaunchedEffect(content) {
                    scrollState.animateScrollTo(scrollState.maxValue)
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 4.dp)
                        .background(
                            color = primaryColor.copy(alpha = 0.02f),
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                        )
                ) {
                    BasicTextField(
                        value = content,
                        onValueChange = { content = it },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = textPrimary,
                            lineHeight = 24.sp
                        ),
                        cursorBrush = SolidColor(primaryColor),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        decorationBox = { innerTextField ->
                            if (content.isEmpty()) {
                                Text(
                                    text = "写点什么...",
                                    color = textTertiary,
                                    fontSize = 16.sp
                                )
                            }
                            innerTextField()
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