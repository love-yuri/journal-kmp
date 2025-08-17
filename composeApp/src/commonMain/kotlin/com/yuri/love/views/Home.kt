package com.yuri.love.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import journal.composeapp.generated.resources.MapleMono_NF_CN_Medium
import journal.composeapp.generated.resources.Res
import journal.composeapp.generated.resources.kiss
import journal.composeapp.generated.resources.more
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Month
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview


//val MapleMonoFont = FontFamily(
//    Font(Res.font.MapleMono_NF_CN_Medium)
//)

// 定义 Screen
class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        CreateHome()
    }
}

val softPinkGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFCE7F3), // 浅粉色
        Color(0xFFFEFBFF), // 极淡的粉白色
        Color(0xFFFFFFFF), // 纯白色
        Color(0xFFFFF0F3)  // 极淡的玫瑰白
    ),
    start = Offset.Zero,
    end = Offset.Infinite
)

@Preview
@Composable
private fun CreateHome() {
    val log = logger {}

    Column(modifier = Modifier
        .fillMaxSize()
        .background(softPinkGradient)
        .padding(top = 35.dp, start = 10.dp, end = 10.dp)
    ) {
        TapBar()
        DiaryHeaderAdvanced()
        JournalCardComposable()
    }
}

@Composable
fun JournalCardComposable() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
        ,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF10B981)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(modifier = Modifier.padding(all = 8.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.CenterStart),
                    text = "美好的一天",
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
//                    fontFamily = FontFamily(
//                        Font(Res.font.MapleMono_NF_CN_Medium)
//                    )
                )
                Canvas(modifier = Modifier.size(16.dp).align(Alignment.CenterEnd)) {
                    drawCircle(
                        color = Color.Gray,
                        radius = size.minDimension / 2,
                        style = Fill
                    )
                }
            }
            Text("yuri")
        }
    }
}

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
            // 主标题 - 带有字符逐个显示效果
            AnimatedText(
                text = "今天想记录什么？",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    fontFamily = FontFamily(
                        Font(Res.font.MapleMono_NF_CN_Medium)
                    )
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
                    fontFamily = FontFamily(
                        Font(Res.font.MapleMono_NF_CN_Medium)
                    )
                )
            }
        }
    }
}

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

@Composable
private fun TapBar() {
    Box( modifier = Modifier.fillMaxWidth().height(50.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color(0xFFFDF2F8).copy(alpha = 0.8f),
                )
                .blur(radius = 10.dp)
        )
        Box(modifier = Modifier.padding(start = 3.dp, end = 3.dp).fillMaxSize()) {
            Image(painterResource(Res.drawable.more),
                null,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(24.dp, 24.dp)
            )
            Text("Journal",
                Modifier.align(Alignment.Center),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEC4899)
            )
            Image(painterResource(Res.drawable.kiss),
                null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(28.dp, 28.dp)
            )
        }
    }
}