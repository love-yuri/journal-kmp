package com.yuri.love.views.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuri.love.share.GlobalFonts
import kotlinx.coroutines.delay

/**
 * topbar 快速跳动的提示
 */
@Composable
fun DiaryHeaderAdvanced() {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(0) // 稍微延迟启动动画
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        ) + slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        ) + scaleIn(
            initialScale = 0.95f,
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp, top = 5.dp)
        ) {
            AnimatedText(
                text = "今天想记录什么？",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    fontFamily = GlobalFonts.MapleMonoFont
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 副标题 - 稍微延迟显示
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 800,
                        delayMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                Text(
                    text = "记录生活的美好瞬间，让回忆永远珍藏",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF4B5563)
                    ),
                    fontFamily = GlobalFonts.MapleMonoFont
                )
            }
        }
    }
}
