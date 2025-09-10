package com.yuri.love.views.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.yuri.love.share.GlobalValue
import com.yuri.love.utils.platformSafeTopPadding
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.*

class CreateScreen: Screen {
    @Preview
    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    override fun Content() {
        val navigator = LocalNavigator.current

        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        val scrollState = rememberScrollState()
        val currentDate = remember {
            SimpleDateFormat("MM/dd", Locale.getDefault()).format(Date())
        }

        // 简洁的配色
        val primaryColor = Color(0xFFFFB6C1)
        val backgroundColor = Color(0xFFFFFFFF)
        val textPrimary = Color(0xFF111827)
        val textSecondary = Color(0xFF6B7280)
        val textTertiary = Color(0xFF9CA3AF)
        val dividerColor = Color(0xFFE5E7EB)

        Column(
            modifier = Modifier
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
                        onClick = { navigator?.pop() },
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
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = {
                        Text(
                            "标题",
                            fontSize = 28.sp,
                            color = textTertiary,
                            fontWeight = FontWeight.Normal
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = primaryColor
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        lineHeight = 36.sp
                    )
                )

                // 标题下分割线
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(primaryColor)
                )

                Spacer(modifier = Modifier.height(6.dp))

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
                            fontSize = 17.sp,
                            color = textPrimary,
                            lineHeight = 26.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        cursorBrush = SolidColor(primaryColor),
                        modifier = Modifier.fillMaxSize(),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 8.dp)
                            ) {
                                if (content.isEmpty()) {
                                    Text(
                                        text = "写点什么...",
                                        color = textTertiary,
                                        fontSize = 17.sp,
                                        lineHeight = 26.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }

            // 底部状态栏
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
    }
}