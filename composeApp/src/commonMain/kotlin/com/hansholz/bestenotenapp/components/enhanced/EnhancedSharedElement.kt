package com.hansholz.bestenotenapp.components.enhanced

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.OverlayClip
import androidx.compose.animation.SharedTransitionScope.PlaceholderSize.Companion.ContentSize
import androidx.compose.animation.SharedTransitionScope.ResizeMode
import androidx.compose.animation.SharedTransitionScope.SharedContentState
import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.hansholz.bestenotenapp.theme.LocalAnimationsEnabled

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.enhancedSharedElement(
    sharedTransitionScope: SharedTransitionScope,
    sharedContentState: SharedContentState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    boundsTransform: BoundsTransform = BoundsTransform { _, _ -> spring(stiffness = StiffnessMediumLow, visibilityThreshold = Rect.VisibilityThreshold) },
    placeholderSize: SharedTransitionScope.PlaceholderSize = ContentSize,
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
    clipInOverlayDuringTransition: OverlayClip = ParentClip,
): Modifier =
    with(sharedTransitionScope) {
        if (LocalAnimationsEnabled.current.value) {
            this@enhancedSharedElement.sharedElement(
                sharedContentState = sharedContentState,
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = boundsTransform,
                placeholderSize = placeholderSize,
                renderInOverlayDuringTransition = renderInOverlayDuringTransition,
                zIndexInOverlay = zIndexInOverlay,
                clipInOverlayDuringTransition = clipInOverlayDuringTransition,
            )
        } else {
            this@enhancedSharedElement
        }
    }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.enhancedSharedBounds(
    sharedTransitionScope: SharedTransitionScope,
    sharedContentState: SharedContentState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
    boundsTransform: BoundsTransform = BoundsTransform { _, _ -> spring(stiffness = StiffnessMediumLow, visibilityThreshold = Rect.VisibilityThreshold) },
    resizeMode: ResizeMode = ResizeMode.scaleToBounds(ContentScale.FillWidth, Center),
    placeholderSize: SharedTransitionScope.PlaceholderSize = ContentSize,
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
    clipInOverlayDuringTransition: OverlayClip = ParentClip,
): Modifier =
    with(sharedTransitionScope) {
        if (LocalAnimationsEnabled.current.value) {
            this@enhancedSharedBounds.sharedBounds(
                sharedContentState = sharedContentState,
                animatedVisibilityScope = animatedVisibilityScope,
                enter = enter,
                exit = exit,
                boundsTransform = boundsTransform,
                resizeMode = resizeMode,
                placeholderSize = placeholderSize,
                renderInOverlayDuringTransition = renderInOverlayDuringTransition,
                zIndexInOverlay = zIndexInOverlay,
                clipInOverlayDuringTransition = clipInOverlayDuringTransition,
            )
        } else {
            this@enhancedSharedBounds
        }
    }

@ExperimentalSharedTransitionApi
private val ParentClip: OverlayClip =
    object : OverlayClip {
        override fun getClipPath(
            sharedContentState: SharedContentState,
            bounds: Rect,
            layoutDirection: LayoutDirection,
            density: Density,
        ): Path? = sharedContentState.parentSharedContentState?.clipPathInOverlay
    }
