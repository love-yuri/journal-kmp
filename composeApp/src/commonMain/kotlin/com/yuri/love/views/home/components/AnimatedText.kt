package com.yuri.love.views.home.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay

/**
 * 动画显示字符
 */
@Composable
fun AnimatedText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    var visibleChars by remember { mutableIntStateOf(0) }

    LaunchedEffect(text) {
        for (i in 0..text.length) {
            visibleChars = i
            delay(50) // 每个字符间隔50ms显示
        }
    }

    Text(
        text = text.take(visibleChars),
        style = style,
        modifier = modifier
    )
}