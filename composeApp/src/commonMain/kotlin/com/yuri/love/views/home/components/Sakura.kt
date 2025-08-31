import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import journal.composeapp.generated.resources.Res
import journal.composeapp.generated.resources.sakura
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
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
    val movementFunctions: SakuraMovement
) {
    var x by mutableFloatStateOf(x)
    var y by mutableFloatStateOf(y)
    var rotation by mutableFloatStateOf(rotation)

    fun update(screenWidth: Int, screenHeight: Int) {
        x = movementFunctions.updateX(x, y)
        y = movementFunctions.updateY(x, y)
        rotation = movementFunctions.updateRotation(rotation)

        // 重置位置当樱花飞出屏幕时
        if (x !in 0.0..screenWidth.toDouble() || y > screenHeight || y < 0) {
            if (Random.nextFloat() > 0.4f) {
                x = Random.nextFloat() * screenWidth
                y = 0f
            } else {
                x = screenWidth.toFloat()
                y = Random.nextFloat() * screenHeight
            }
        }
    }
}

/**
 * 樱花运动函数
 */
data class SakuraMovement(
    private val horizontalSpeed: Float,
    private val verticalSpeed: Float,
    private val rotationSpeed: Float
) {
    fun updateX(x: Float, y: Float): Float {
        return x + horizontalSpeed - 1.7f
    }

    fun updateY(x: Float, y: Float): Float {
        return y + verticalSpeed
    }

    fun updateRotation(rotation: Float): Float {
        return rotation + rotationSpeed
    }
}

private fun createRandomSakura(screenWidth: Int, screenHeight: Int): SakuraState {
    val x = Random.nextFloat() * screenWidth
    val y = Random.nextFloat() * screenHeight
    val size = 0.8f + Random.nextFloat() * 0.5f  // 0.5 到 1.0
    val rotation = Random.nextFloat() * 6f

    val movement = SakuraMovement(
        horizontalSpeed = -0.5f + Random.nextFloat(), // -0.5 到 0.5
        verticalSpeed = 1.5f + Random.nextFloat() * 0.7f, // 1.5 到 2.2
        rotationSpeed = Random.nextFloat() * 0.03f
    )

    return SakuraState(x, y, size, rotation, movement)
}

/**
 * 主要的樱花动画组合函数 - 性能优化版本
 */
@Composable
fun SakuraAnimation(
    modifier: Modifier = Modifier,
    sakuraCount: Int = 10,
    animationSpeed: Long = 20L
) {
    // 使用 remember 保存樱花状态列表，避免重新创建
    var sakuraList by remember { mutableStateOf<List<SakuraState>>(emptyList()) }
    var containerSize by remember { mutableStateOf(IntOffset.Zero) }

    // 初始化樱花列表
    LaunchedEffect(containerSize, sakuraCount) {
        if (containerSize.x > 0 && containerSize.y > 0 && sakuraList.isEmpty()) {
            sakuraList = List(sakuraCount) {
                createRandomSakura(containerSize.x, containerSize.y)
            }
        }
    }

    // 动画循环 - 只更新各个樱花的内部状态
    LaunchedEffect(sakuraList.size) {
        if (sakuraList.isNotEmpty()) {
            while (true) {
                sakuraList.forEach { sakura ->
                    sakura.update(containerSize.x, containerSize.y)
                }
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
        sakuraList.forEach { sakura ->
            key(sakura) {
                SakuraCompose(sakura)
            }
        }
    }
}

@Composable
fun SakuraCompose(sakura: SakuraState) {
    // 只有这个特定樱花的位置改变时，才会重组这个组件
    Image(
        painter = painterResource(Res.drawable.sakura),
        contentDescription = null,
        modifier = Modifier
            .offset { IntOffset(sakura.x.toInt(), sakura.y.toInt()) } // 先偏移到目标位置
            .size((30 * sakura.size).dp)
            .graphicsLayer(
                rotationZ = sakura.rotation * 180f / kotlin.math.PI.toFloat()
            )
    )
}