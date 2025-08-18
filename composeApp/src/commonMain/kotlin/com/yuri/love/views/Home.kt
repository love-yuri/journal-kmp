package com.yuri.love.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import journal.composeapp.generated.resources.MapleMono_NF_CN_Medium
import journal.composeapp.generated.resources.Res
import journal.composeapp.generated.resources.date
import journal.composeapp.generated.resources.kiss
import journal.composeapp.generated.resources.more
import journal.composeapp.generated.resources.weather_sun
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun Modifier.platformSafeTopPadding(): Modifier {
    if (!LocalInspectionMode.current) {
        this.statusBarsPadding()
    }
    return this
}

val MapleMonoFont: FontFamily
    @Composable
    get() = if (LocalInspectionMode.current) {
        FontFamily.Default
    } else {
        FontFamily(Font(Res.font.MapleMono_NF_CN_Medium))
    }

// 定义 Screen
class HomeScreen : Screen {
    @Composable
    override fun Content() {
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
    Column(modifier = Modifier
        .fillMaxSize()
        .background(softPinkGradient)
        .padding(top = 0.dp, start = 10.dp, end = 10.dp)
        .platformSafeTopPadding()
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
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(modifier = Modifier.padding(all = 12.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.CenterStart).padding(bottom = 5.dp),
                    text = "美好的一天",
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    fontFamily = MapleMonoFont
                )
                Canvas(modifier = Modifier.size(12.dp).align(Alignment.CenterEnd)) {
                    drawCircle(
                        color = Color(0xFFF472B6),
                        style = Fill
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painterResource(Res.drawable.date),
                    null,
                    modifier = Modifier.size(18.dp),
                )
                Text("2024年8月14日", modifier = Modifier.padding(start = 8.dp, end = 12.dp), fontFamily = MapleMonoFont)
                Image(
                    painterResource(Res.drawable.weather_sun),
                    null,
                    modifier = Modifier.size(18.dp),
                )
                Text("晴天", modifier = Modifier.padding(start = 8.dp), fontFamily = MapleMonoFont)
            }
            Row(
                modifier = Modifier.padding(top = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically  // 垂直居中
            ) {
                // 标签 1
                Text(
                    text = "#生活",
                    style = TextStyle(
                        color = Color(0xFF9E3B46),  // text-pink-800
                        fontSize = 13.sp,  // text-xs
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .background(Color(0xFFFEC7D7), shape = CircleShape)  // bg-pink-100 + rounded-full
                        .padding(horizontal = 12.dp, vertical = 3.dp)  // 内外边距
                )

                // 标签 2
                Text(
                    text = "#心情",
                    style = TextStyle(
                        color = Color(0xFF9B2D20),  // text-pink-800
                        fontSize = 13.sp,  // text-xs
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .background(Color(0xFFFEC7D7), shape = CircleShape)  // bg-pink-100 + rounded-full
                        .padding(horizontal = 12.dp, vertical = 3.dp)  // 内外边距
                )
            }
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
                    fontFamily = MapleMonoFont
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
                    fontFamily = MapleMonoFont
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