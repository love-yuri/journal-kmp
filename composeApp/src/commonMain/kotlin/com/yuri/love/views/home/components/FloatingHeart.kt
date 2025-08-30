package com.yuri.love.views.home.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.CurrentScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random

/**
 * 浮动爱心
 */
data class HeartData(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val delay: Int
)


@Composable
fun FloatingHearts() {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var hearts by remember { mutableStateOf(emptyList<HeartData>()) }
    Box(Modifier.fillMaxSize().onGloballyPositioned { coordinates ->
        containerSize = coordinates.size
        if (hearts.isEmpty() && containerSize.width > 0 && containerSize.height > 0) {
            hearts = List(28) { index ->
                HeartData(
                    x = Random.nextFloat() * containerSize.width,
                    y = Random.nextFloat() * containerSize.height,
                    size = Random.nextFloat() * 20 + 10,
                    speed = Random.nextFloat() * 0.5f + 0.2f,
                    delay = Random.nextInt(8) * 1000
                )
            }
        }
    }) {
        hearts.forEach { heart ->
            FloatingHeart(heart)
        }
    }
}

@Composable
private fun FloatingHeart(heartData: HeartData) {
    val infiniteTransition = rememberInfiniteTransition()

    val animatedY by infiniteTransition.animateFloat(
        initialValue = heartData.y,
        targetValue = heartData.y - 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (8000 / heartData.speed).toInt(),
                delayMillis = heartData.delay,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
    )

    val animatedX by infiniteTransition.animateFloat(
        initialValue = heartData.x,
        targetValue = heartData.x - 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (8000 / heartData.speed).toInt(),
                delayMillis = heartData.delay,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = (8000 / heartData.speed).toInt()
                0f at 0
                0.6f at 1000
                0.4f at 4000
                0f at 8000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "heartAlpha"
    )

    Box(
        modifier = Modifier
            .offset(animatedX.dp, animatedY.dp)
            .size(heartData.size.dp)
            .alpha(alpha)

            .drawWithContent {
                drawHeart(
                    color = Color(0xFFFF6B9D),
                    size = size.minDimension
                )
            }
    )
}

fun DrawScope.drawHeart(color: Color, size: Float) {
    val path = Path().apply {
        val width = size
        val height = size

        moveTo(width / 2, height * 0.25f)

        // 左半心
        cubicTo(
            width * 0.2f, height * 0.1f,
            -width * 0.25f, height * 0.6f,
            width / 2, height
        )

        // 右半心
        cubicTo(
            width * 1.25f, height * 0.6f,
            width * 0.8f, height * 0.1f,
            width / 2, height * 0.25f
        )

        close()
    }

    drawPath(path, color)
}
