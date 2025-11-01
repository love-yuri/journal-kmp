package com.yuri.love.views.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.yuri.love.Journal
import com.yuri.love.components.DeleteConfirmDialog
import com.yuri.love.database.JournalService
import com.yuri.love.utils.platformSafeTopPadding
import com.yuri.love.views.home.components.DiaryHeaderAdvanced
import com.yuri.love.views.home.components.JournalCardComposable
import com.yuri.love.components.TopBar
import com.yuri.love.share.GlobalStyle
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 主页 - 优化启动体验版本
 */
class HomeScreen: Screen {
    @Composable
    override fun Content() {
        // 收集StateFlow状态
        val journals by JournalService.journals.collectAsState()
        CreateHome(journals)
    }
}

@Suppress("RememberReturnType")
@Preview
@Composable
private fun CreateHome(journals: List<Journal>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GlobalStyle.tapBarBackground)
            .platformSafeTopPadding()
    ) {
        // TapBar
        TopBar()

        // 主内容区域
        Column(
            modifier = Modifier
                .background(GlobalStyle.softPinkGradient)
                .padding(start = 10.dp, end = 10.dp)
                .weight(1f)
        ) {
            DiaryHeaderAdvanced()
            JournalListScreen(journals)
        }
    }
}
@OptIn(FlowPreview::class)
@Composable
fun JournalListScreen(journals: List<Journal>) {
    val listState = rememberLazyListState()
    var isLoadingMore by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    var selectedJournal by remember { mutableStateOf<Journal?>(null) }

    var y by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    // 下拉刷新阈值
    val refreshThreshold = 140f
    val maxPullDistance = 200f

    val animatedOffset by animateFloatAsState(
        targetValue = if (isRefreshing) refreshThreshold else y,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    LaunchedEffect(listState, journals) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .debounce(100)
            .collect { visibleItems ->
                if (visibleItems.isNotEmpty() && !isLoadingMore) {
                    val lastVisibleItem = visibleItems.last()
                    if (lastVisibleItem.index == journals.size) {
                        isLoadingMore = true
                        JournalService.nextPage()
                        isLoadingMore = false
                    }
                }
            }
    }

    // 下拉刷新状态
    val pullRefreshState = remember {
        derivedStateOf {
            when {
                isRefreshing -> RefreshState.Refreshing
                y >= refreshThreshold -> RefreshState.ReadyToRefresh
                y > 0 -> RefreshState.Pulling
                else -> RefreshState.Idle
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        var dragStartY = down.position.y

                        do {
                            val event = awaitPointerEvent()
                            event.changes.forEach { change ->
                                if (listState.firstVisibleItemIndex == 0 &&
                                    listState.firstVisibleItemScrollOffset <= 0) {

                                    val dragAmount = change.position.y - dragStartY
                                    val newY = (y + dragAmount * 0.4f).coerceAtLeast(0f)
                                    y = newY.coerceAtMost(maxPullDistance)

                                    dragStartY = change.position.y
                                }
                            }
                        } while (event.changes.any { it.pressed })

                        if (y >= refreshThreshold && !isRefreshing) {
                            scope.launch {
                                isRefreshing = true
                                JournalService.refresh()
                                delay(300)
                                isRefreshing = false
                                y = 0f
                            }
                        } else {
                            y = 0f
                        }
                    }
                }
            }
    ) {
        // 下拉刷新指示器
        item {
            ElegantPullRefreshIndicator(
                offset = animatedOffset,
                refreshState = pullRefreshState.value,
                refreshThreshold = refreshThreshold
            )
        }

        items(journals, key = { it.id }) { journal ->
            AnimatedJournalItem(journal) {
                selectedJournal = journal
            }
        }
    }

    // 删除确认对话框
    DeleteConfirmDialog(
        visible = selectedJournal != null,
        title = "确认删除",
        message = "确定要删除《${selectedJournal?.title?.takeIf { it.isNotEmpty() } ?: "这篇日记"}》吗？\n此操作无法撤销。",
        onConfirm = {
            JournalService.delete(selectedJournal!!)
        },
        onDismiss = {
            selectedJournal = null
        }
    )
}

@Composable
fun AnimatedJournalItem(
    journal: Journal,
    onDelete: ((Journal) -> Unit)? = null
) {
    var hasAppeared by remember { mutableStateOf(false) }

    val offsetY by animateDpAsState(
        targetValue = if (hasAppeared) 0.dp else 60.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
    )

    val alpha by animateFloatAsState(
        targetValue = if (hasAppeared) 1f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
    )

    LaunchedEffect(journal.id) {
        hasAppeared = true
    }

    Box(
        modifier = Modifier
            .offset(y = offsetY)
            .alpha(alpha)
    ) {
        JournalCardComposable(journal, onDelete)
    }
}

enum class RefreshState {
    Idle, Pulling, ReadyToRefresh, Refreshing
}

@Composable
fun ElegantPullRefreshIndicator(
    offset: Float,
    refreshState: RefreshState,
    refreshThreshold: Float
) {
    val progress = (offset / refreshThreshold).coerceIn(0f, 1f)

    // 图标旋转动画
    val iconRotation by animateFloatAsState(
        targetValue = when (refreshState) {
            RefreshState.Refreshing -> 360f * 10 // 持续旋转
            RefreshState.ReadyToRefresh -> 180f
            else -> progress * 180f
        },
        animationSpec = if (refreshState == RefreshState.Refreshing) {
            infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        } else {
            spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        }
    )

    // 渐变色彩
    val gradientColors = when (refreshState) {
        RefreshState.ReadyToRefresh -> listOf(
            Color(0xFF6B73FF), Color(0xFF9B59B6)
        )
        RefreshState.Refreshing -> listOf(
            Color(0xFF00C9FF), Color(0xFF92FE9D)
        )
        else -> listOf(
            Color(0xFFE8E8E8), Color(0xFFBDBDBD)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(1.dp)
            .height(offset.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.1f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = offset > 10f,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 刷新圆圈指示器
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = gradientColors,
                                radius = 24f
                            ),
                            shape = CircleShape
                        )
                        .padding(2.dp)
                        .background(
                            Color.White.copy(alpha = 0.9f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // 将图标替换为简单的几何图形或文字
                    Text(
                        text = when (refreshState) {
                            RefreshState.Refreshing -> "⟳"
                            RefreshState.ReadyToRefresh -> "↑"
                            else -> "↓"
                        },
                        fontSize = 24.sp,
                        modifier = Modifier.rotate(iconRotation)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 状态文字
                Text(
                    text = when (refreshState) {
                        RefreshState.Idle -> ""
                        RefreshState.Pulling -> "下拉刷新"
                        RefreshState.ReadyToRefresh -> "释放刷新"
                        RefreshState.Refreshing -> "刷新中..."
                    },
                    color = when (refreshState) {
                        RefreshState.ReadyToRefresh -> Color(0xFF9B59B6)
                        RefreshState.Refreshing -> Color(0xFF00C9FF)
                        else -> Color.Gray
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                // 进度指示器
                if (refreshState == RefreshState.Pulling) {
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .width(60.dp)
                            .height(2.dp),
                        color = Color(0xFF6B73FF),
                        trackColor = Color.Gray.copy(alpha = 0.2f)
                    )
                }
            }
        }

        // 水滴效果（可选）
        if (offset > 20f && refreshState != RefreshState.Refreshing) {
            WaterDropEffect(progress = progress)
        }
    }
}

@Composable
fun WaterDropEffect(progress: Float) {
    val dropSize by animateFloatAsState(
        targetValue = (8 + progress * 12),
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(progress * 0.6f),
        contentAlignment = Alignment.TopCenter
    ) {
        Canvas(
            modifier = Modifier
                .size(dropSize.dp)
                .offset(y = (-dropSize / 2).dp)
        ) {
            val radius = size.minDimension / 2
            drawCircle(
                color = Color(0xFF6B73FF).copy(alpha = 0.3f),
                radius = radius,
                center = center
            )
        }
    }
}