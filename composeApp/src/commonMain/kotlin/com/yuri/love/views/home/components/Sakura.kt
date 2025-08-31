package com.yuri.love.views.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import journal.composeapp.generated.resources.Res
import journal.composeapp.generated.resources.sakura
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import kotlin.math.*
import kotlin.random.Random

/**
 * 樱花数据类 - 使用 MutableState 实现单独更新
 */
@Stable
class SakuraState(
    x: Float,
    y: Float,
    val size: Float,
    rotation: Float,
    val movementFunctions: SakuraMovement,
    val alpha: Float = 0.7f + Random.nextFloat() * 0.3f
) {
    var x by mutableFloatStateOf(x)
    var y by mutableFloatStateOf(y)
    var rotation by mutableFloatStateOf(rotation)
    var velocityX by mutableFloatStateOf(Random.nextFloat() * 0.8f - 0.4f)
    var velocityY by mutableFloatStateOf(0.8f + Random.nextFloat() * 0.6f)
    var swingTime by mutableFloatStateOf(Random.nextFloat() * PI.toFloat() * 2)

    fun update(screenWidth: Int, screenHeight: Int, deltaTime: Float = 1f, windForce: Float = 0f) {
        // 物理效果
        velocityY += 0.01f * deltaTime // 重力
        velocityX += windForce * 0.01f * deltaTime // 风力

        // 空气阻力
        velocityX *= 0.998f
        velocityY *= 0.999f

        // 摆动效果
        swingTime += 0.03f * deltaTime
        val swingOffset = sin(swingTime) * 0.8f

        // 更新位置
        x += (velocityX + swingOffset) * deltaTime
        y += velocityY * deltaTime
        rotation += movementFunctions.rotationSpeed * deltaTime

        // 检查是否需要重置 - 单个樱花独立重置
        if (shouldReset(screenWidth, screenHeight)) {
            resetPosition(screenWidth, screenHeight)
        }
    }

    private fun shouldReset(screenWidth: Int, screenHeight: Int): Boolean {
        return x < -80f || x > screenWidth + 80f || y > screenHeight + 50f
    }

    private fun resetPosition(screenWidth: Int, screenHeight: Int) {
        val spawnChoice = Random.nextFloat()
        when {
            spawnChoice < 0.6f -> {
                // 主要从上方进入 (60%)
                x = Random.nextFloat() * (screenWidth + 200f) - 100f // 稍微超出屏幕边界
                y = -Random.nextFloat() * 150f - 50f
                velocityX = Random.nextFloat() * 1.2f - 0.6f
                velocityY = 1.0f + Random.nextFloat() * 0.8f
            }
            spawnChoice < 0.8f -> {
                // 从右上角进入 (20%)
                x = screenWidth + Random.nextFloat() * 100f + 30f
                y = -Random.nextFloat() * 200f
                velocityX = -(0.5f + Random.nextFloat() * 1.0f)
                velocityY = 0.8f + Random.nextFloat() * 1.0f
            }
            else -> {
                // 从左上角进入 (20%)
                x = -Random.nextFloat() * 100f - 30f
                y = -Random.nextFloat() * 200f
                velocityX = 0.3f + Random.nextFloat() * 1.0f
                velocityY = 0.8f + Random.nextFloat() * 1.0f
            }
        }

        // 重置物理属性
        swingTime = Random.nextFloat() * PI.toFloat() * 2
        rotation = Random.nextFloat() * 360f
    }
}

/**
 * 樱花运动函数 - 简化版本
 */
data class SakuraMovement(
    val rotationSpeed: Float
)

private fun createRandomSakura(screenWidth: Int, screenHeight: Int): SakuraState {
    // 初始位置分布更合理
    val startFromTop = Random.nextFloat() < 0.7f
    val x = if (startFromTop) {
        Random.nextFloat() * (screenWidth + 200f) - 100f
    } else {
        if (Random.nextFloat() < 0.5f) screenWidth + 100f else -100f
    }

    val y = if (startFromTop) {
        -Random.nextFloat() * 300f - 100f // 从更高的地方开始
    } else {
        Random.nextFloat() * screenHeight * 0.3f
    }

    val size = 0.6f + Random.nextFloat() * 0.8f
    val rotation = Random.nextFloat() * 360f

    val movement = SakuraMovement(
        rotationSpeed = Random.nextFloat() * 4f - 2f // 增加旋转变化
    )

    return SakuraState(x, y, size, rotation, movement)
}

/**
 * 主要的樱花动画组合函数 - 性能优化版本
 */
@Composable
fun SakuraAnimation(
    modifier: Modifier = Modifier,
    sakuraCount: Int = 15,
    animationSpeed: Long = 16L // 60fps
) {
    var sakuraList by remember { mutableStateOf<List<SakuraState>>(emptyList()) }
    var containerSize by remember { mutableStateOf(IntOffset.Zero) }

    // 风力效果
    var windPhase by remember { mutableFloatStateOf(0f) }

    // 初始化樱花列表
    LaunchedEffect(containerSize, sakuraCount) {
        if (containerSize.x > 0 && containerSize.y > 0 && sakuraList.isEmpty()) {
            sakuraList = List(sakuraCount) {
                createRandomSakura(containerSize.x, containerSize.y)
            }
        }
    }

    // 动画循环 - 优化版本，确保樱花连续性
    LaunchedEffect(sakuraList.size) {
        if (sakuraList.isNotEmpty()) {
            var lastTime = System.currentTimeMillis()
            while (true) {
                val currentTime = System.currentTimeMillis()
                val deltaTime = if (lastTime == 0L) 1f else (currentTime - lastTime) / 16f

                // 更新风力
                windPhase += 0.008f // 稍微慢一点的风力变化
                val currentWind = sin(windPhase) * 0.6f

                // 更新所有樱花 - 每个樱花独立更新和重置
                sakuraList.forEach { sakura ->
                    sakura.update(containerSize.x, containerSize.y, deltaTime, currentWind)
                }

                lastTime = currentTime
                delay(animationSpeed)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                val newSize = IntOffset(coordinates.size.width, coordinates.size.height)
                if (containerSize != newSize) {
                    containerSize = newSize
                }
            }
    ) {
        // 渲染樱花
        sakuraList.forEach { sakura ->
            SakuraCompose(sakura)
        }
    }
}

@Composable
fun SakuraCompose(sakura: SakuraState) {
    Image(
        painter = painterResource(Res.drawable.sakura),
        contentDescription = null,
        modifier = Modifier
            .offset { IntOffset(sakura.x.toInt(), sakura.y.toInt()) }
            .size((35 * sakura.size).dp)
            .alpha(sakura.alpha)
            .graphicsLayer(
                rotationZ = sakura.rotation,
                scaleX = 0.8f + sin(sakura.swingTime) * 0.1f, // 轻微缩放效果
                scaleY = 0.8f + cos(sakura.swingTime * 1.2f) * 0.1f
            )
    )
}
