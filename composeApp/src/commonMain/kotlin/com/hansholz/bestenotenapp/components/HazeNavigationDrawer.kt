package com.hansholz.bestenotenapp.components

import androidx.annotation.FloatRange
import androidx.annotation.IntDef
import androidx.annotation.RestrictTo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.PredictiveBackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch

@Composable
fun CloseableNavigationDrawer(
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    content: @Composable () -> Unit
) {
    Row(modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = drawerState.targetValue == DrawerValue.Open,
            enter = fadeIn() + expandHorizontally(spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow)),
        ) {
            drawerContent()
        }
        content()
    }
}

@Composable
fun HazeModalDrawerSheet(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    hazeState: HazeState,
    drawerShape: Shape = DrawerDefaults.shape,
    drawerContainerColor: Color = DrawerDefaults.modalContainerColor,
    drawerContentColor: Color = contentColorFor(drawerContainerColor),
    drawerTonalElevation: Dp = DrawerDefaults.ModalDrawerElevation,
    windowInsets: WindowInsets = DrawerDefaults.windowInsets,
    content: @Composable ColumnScope.() -> Unit
) {
    DrawerPredictiveBackHandler(drawerState) { drawerPredictiveBackState ->
        DrawerSheet(
            drawerPredictiveBackState = drawerPredictiveBackState,
            windowInsets = windowInsets,
            modifier = modifier,
            hazeState = hazeState,
            drawerShape = drawerShape,
            drawerContainerColor = drawerContainerColor,
            drawerContentColor = drawerContentColor,
            drawerTonalElevation = drawerTonalElevation,
            drawerOffset = { drawerState.currentOffset },
            content = content
        )
    }
}

@Composable
internal fun DrawerSheet(
    drawerPredictiveBackState: DrawerPredictiveBackState?,
    windowInsets: WindowInsets,
    modifier: Modifier = Modifier,
    hazeState: HazeState,
    drawerShape: Shape = RectangleShape,
    drawerContainerColor: Color = DrawerDefaults.standardContainerColor,
    drawerContentColor: Color = contentColorFor(drawerContainerColor),
    drawerTonalElevation: Dp = DrawerDefaults.PermanentDrawerElevation,
    drawerOffset: FloatProducer = FloatProducer { 0F },
    content: @Composable ColumnScope.() -> Unit
) {
    val density = LocalDensity.current
    val maxWidth = 360.0.dp
    val maxWidthPx = with(density) { maxWidth.toPx() }
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val predictiveBackDrawerContainerModifier =
        if (drawerPredictiveBackState != null) {
            Modifier.predictiveBackDrawerContainer(drawerPredictiveBackState)
        } else {
            Modifier
        }
    Surface(
        modifier =
            modifier
                .sizeIn(minWidth = MinimumDrawerWidth, maxWidth = maxWidth)
                // Scale up the Surface horizontally in case the drawer offset it greater than zero.
                // This is done to avoid showing a gap when the drawer opens and bounces when it's
                // applied with a bouncy motion. Note that the content inside the Surface is scaled
                // back down to maintain its aspect ratio (see below).
                .horizontalScaleUp(
                    drawerOffset = drawerOffset,
                    drawerWidth = maxWidthPx,
                    isRtl = isRtl
                )
                .then(predictiveBackDrawerContainerModifier)
                .fillMaxHeight()
                .clip(RoundedCornerShape(topEnd = 30.dp, bottomEnd = 30.dp))
                .enhancedHazeEffect(hazeState, colorScheme.surfaceContainerHighest),
        shape = drawerShape,
        color = drawerContainerColor,
        contentColor = drawerContentColor,
        tonalElevation = drawerTonalElevation
    ) {
        val predictiveBackDrawerChildModifier =
            if (drawerPredictiveBackState != null)
                Modifier.predictiveBackDrawerChild(drawerPredictiveBackState)
            else Modifier
        Column(
            Modifier.sizeIn(minWidth = MinimumDrawerWidth, maxWidth = maxWidth)
                // Scale the content down in case the drawer offset is greater than one. The
                // wrapping Surface is scaled up, so this is done to maintain the content's aspect
                // ratio.
                .horizontalScaleDown(
                    drawerOffset = drawerOffset,
                    drawerWidth = maxWidthPx,
                    isRtl = isRtl
                )
                .then(predictiveBackDrawerChildModifier)
                .windowInsetsPadding(windowInsets),
            content = content
        )
    }
}

/**
 * A [Modifier] that scales up the drawing layer on the X axis in case the [drawerOffset] is greater
 * than zero. The scaling will ensure that there is no visible gap between the drawer and the edge
 * of the screen in case the drawer bounces when it opens due to a more expressive motion setting.
 *
 * A [horizontalScaleDown] should be applied to the content of the drawer to maintain the content
 * aspect ratio as the container scales up.
 *
 * @see horizontalScaleDown
 */
private fun Modifier.horizontalScaleUp(
    drawerOffset: FloatProducer,
    drawerWidth: Float,
    isRtl: Boolean
) = graphicsLayer {
    val offset = drawerOffset()
    scaleX = if (offset > 0f) 1f + offset / drawerWidth else 1f
    transformOrigin = TransformOrigin(if (isRtl) 0f else 1f, 0.5f)
}

/**
 * A [Modifier] that scales down the drawing layer on the X axis in case the [drawerOffset] is
 * greater than zero. This modifier should be applied to the content inside a component that was
 * scaled up with a [horizontalScaleUp] modifier. It will ensure that the content maintains its
 * aspect ratio as the container scales up.
 *
 * @see horizontalScaleUp
 */
private fun Modifier.horizontalScaleDown(
    drawerOffset: FloatProducer,
    drawerWidth: Float,
    isRtl: Boolean
) = graphicsLayer {
    val offset = drawerOffset()
    scaleX = if (offset > 0f) 1 / (1f + offset / drawerWidth) else 1f
    transformOrigin = TransformOrigin(if (isRtl) 0f else 1f, 0f)
}

private fun Modifier.predictiveBackDrawerContainer(
    drawerPredictiveBackState: DrawerPredictiveBackState
) = graphicsLayer {
    this.translationX = -drawerPredictiveBackState.scaleYDistance
}

private fun Modifier.predictiveBackDrawerChild(
    drawerPredictiveBackState: DrawerPredictiveBackState
) = graphicsLayer {
    this.translationX = -drawerPredictiveBackState.scaleYDistance
}


/**
 * Registers a [PredictiveBackHandler] and provides animation values in [DrawerPredictiveBackState]
 * based on back progress.
 *
 * @param drawerState state of the drawer
 * @param content content of the rest of the UI
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun DrawerPredictiveBackHandler(
    drawerState: DrawerState,
    content: @Composable (DrawerPredictiveBackState) -> Unit
) {
    val drawerPredictiveBackState = remember { DrawerPredictiveBackState() }
    val scope = rememberCoroutineScope()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val maxScaleXDistanceGrow: Float
    val maxScaleXDistanceShrink: Float
    val maxScaleYDistance: Float
    with(LocalDensity.current) {
        maxScaleXDistanceGrow = PredictiveBackDrawerMaxScaleXDistanceGrow.toPx()
        maxScaleXDistanceShrink = PredictiveBackDrawerMaxScaleXDistanceShrink.toPx()
        maxScaleYDistance = PredictiveBackDrawerMaxScaleYDistance.toPx()
    }

    PredictiveBackHandler(enabled = drawerState.isOpen) { progress ->
        try {
            progress.collect { backEvent ->
                drawerPredictiveBackState.update(
                    PredictiveBack.transform(backEvent.progress),
                    backEvent.swipeEdge == BackEventCompat.EDGE_LEFT,
                    isRtl,
                    maxScaleXDistanceGrow,
                    maxScaleXDistanceShrink,
                    maxScaleYDistance
                )
            }
        } catch (_: kotlin.coroutines.cancellation.CancellationException) {
            drawerPredictiveBackState.clear()
        } finally {
            if (drawerPredictiveBackState.swipeEdgeMatchesDrawer) {
                // If swipe edge matches drawer gravity and we've stretched the drawer horizontally,
                // un-stretch it smoothly so that it hides completely during the drawer close.
                scope.launch {
                    animate(
                        initialValue = drawerPredictiveBackState.scaleXDistance,
                        targetValue = 0f
                    ) { value, _ ->
                        drawerPredictiveBackState.scaleXDistance = value
                    }
                    drawerPredictiveBackState.clear()
                }
            }
            drawerState.close()
        }
    }

    LaunchedEffect(drawerState.isClosed) {
        if (drawerState.isClosed) {
            drawerPredictiveBackState.clear()
        }
    }

    content(drawerPredictiveBackState)
}

@Stable
internal class DrawerPredictiveBackState {

    var swipeEdgeMatchesDrawer by mutableStateOf(true)

    var scaleXDistance by mutableFloatStateOf(0f)

    var scaleYDistance by mutableFloatStateOf(0f)

    fun update(
        progress: Float,
        swipeEdgeLeft: Boolean,
        isRtl: Boolean,
        maxScaleXDistanceGrow: Float,
        maxScaleXDistanceShrink: Float,
        maxScaleYDistance: Float
    ) {
        swipeEdgeMatchesDrawer = swipeEdgeLeft != isRtl
        val maxScaleXDistance =
            if (swipeEdgeMatchesDrawer) maxScaleXDistanceGrow else maxScaleXDistanceShrink
        scaleXDistance = lerp(0f, maxScaleXDistance, progress)
        scaleYDistance = lerp(0f, maxScaleYDistance, progress)
    }

    fun clear() {
        swipeEdgeMatchesDrawer = true
        scaleXDistance = 0f
        scaleYDistance = 0f
    }
}

fun interface FloatProducer {
    operator fun invoke(): Float
}

object PredictiveBack {
    internal fun transform(progress: Float) = CubicBezierEasing(0.1f, 0.1f, 0f, 1f).transform(progress)
}

data class BackEventCompat(
    val touchX: Float,
    val touchY: Float,
    @param:FloatRange(from = 0.0, to = 1.0)
    val progress: Float,
    val swipeEdge: @SwipeEdge Int,
) {
    @Target(AnnotationTarget.TYPE)
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(EDGE_LEFT, EDGE_RIGHT)
    annotation class SwipeEdge

    companion object {
        const val EDGE_LEFT = 0
        const val EDGE_RIGHT = 1
    }
}

private val MinimumDrawerWidth = 240.dp

internal val PredictiveBackDrawerMaxScaleXDistanceGrow = 12.dp
internal val PredictiveBackDrawerMaxScaleXDistanceShrink = 24.dp
internal val PredictiveBackDrawerMaxScaleYDistance = 48.dp
