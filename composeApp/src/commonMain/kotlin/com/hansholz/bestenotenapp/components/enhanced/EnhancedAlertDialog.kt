package com.hansholz.bestenotenapp.components.enhanced

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.PredictiveBackHandler
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.components.FullscreenDialog
import com.hansholz.bestenotenapp.components.clippedShadow
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedVisibility
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedHazeEffect
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrateN
import com.hansholz.bestenotenapp.main.AppHazeState
import com.hansholz.bestenotenapp.main.LocalNativeComponentsEnabled
import com.hansholz.bestenotenapp.main.LocalNativeDialogBackdrop
import com.hansholz.bestenotenapp.main.Platform
import com.hansholz.bestenotenapp.main.getPlatform
import com.hansholz.bestenotenapp.theme.LocalBlurEnabled
import kotlinx.coroutines.delay
import top.ltfan.multihaptic.compose.rememberVibrator
import kotlin.time.Duration.Companion.milliseconds

private val LocalEnhancedDialogDepth = compositionLocalOf { 0 }
private val LocalNativeDialogBoundsReporter = compositionLocalOf<((Rect) -> Unit)?> { null }
private val ActiveEnhancedDialogCount = mutableIntStateOf(0)
private val ActiveEnhancedDialogIds = mutableStateListOf<Int>()
private val ActiveNativeDialogFrames = mutableStateMapOf<Int, Rect>()
private var nextEnhancedDialogId = 0
private val IosDialogEnterEasing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)
private val IosDialogExitEasing = CubicBezierEasing(0.4f, 0f, 1f, 1f)

private fun Rect.isValidNativeDialogFrame() =
    !isEmpty &&
        left.isFinite() &&
        top.isFinite() &&
        right.isFinite() &&
        bottom.isFinite()

@Composable
fun EnhancedAlertDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    maxWidth: Dp? = null,
    withBlur: Boolean = true,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    shadowElevation: Dp = ShadowElevation,
) {
    val nativeComponentsEnabled = LocalNativeComponentsEnabled.current.value
    val blurEnabled = LocalBlurEnabled.current
    CompositionLocalProvider(
        LocalBlurEnabled provides if (withBlur) blurEnabled else mutableStateOf(false),
    ) {
        BasicEnhancedAlertDialog(
            visible = visible,
            onDismissRequest = onDismissRequest,
            content = {
                EnhancedAlertDialogContent(
                    buttons = {
                        FlowRow(
                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    space = ButtonsHorizontalSpacing,
                                    alignment = Alignment.End,
                                ),
                            verticalArrangement =
                                Arrangement.spacedBy(
                                    space = ButtonsVerticalSpacing,
                                    alignment = Alignment.Bottom,
                                ),
                        ) {
                            dismissButton?.invoke()
                            confirmButton()
                        }
                    },
                    icon = icon,
                    title = title,
                    text = text,
                    shape = shape,
                    containerColor = containerColor,
                    shadowElevation = shadowElevation,
                    // Note that a button content color is provided here from the dialog's token, but in
                    // most cases, TextButtons should be used for dismiss and confirm buttons.
                    // TextButtons will not consume this provided content color value, and will used their
                    // own defined or default colors.
                    buttonContentColor = MaterialTheme.colorScheme.primary,
                    iconContentColor = iconContentColor,
                    titleContentColor = titleContentColor,
                    textContentColor = textContentColor,
                    modifier =
                        modifier
                            .sizeIn(
                                minWidth = if (nativeComponentsEnabled) 0.dp else DialogMinWidth,
                                maxWidth = maxWidth ?: DialogMaxWidth,
                            ).then(if (maxWidth == Dp.Unspecified && getPlatform() == Platform.DESKTOP) Modifier.padding(top = 30.dp) else Modifier)
                            .then(Modifier.semantics { paneTitle = "Dialog" }),
                )
            },
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BasicEnhancedAlertDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val vibrator = rememberVibrator()
    val nativeComponentsEnabled = LocalNativeComponentsEnabled.current.value
    val nativeDialogBackdrop = LocalNativeDialogBackdrop.current
    val dialogDepth = LocalEnhancedDialogDepth.current
    val density = LocalDensity.current.density
    val dialogId = remember { nextEnhancedDialogId++ }

    var visibleAnimated by remember { mutableStateOf(false) }

    var scale by remember {
        mutableFloatStateOf(1f)
    }
    val animatedScale by animateFloatAsState(scale)

    LaunchedEffect(visible) {
        if (visible) {
            scale = 1f
            visibleAnimated = true
        } else if (nativeComponentsEnabled) {
            delay(320.milliseconds)
            visibleAnimated = false
        } else if (getPlatform() != Platform.ANDROID) {
            delay(200.milliseconds)
            visibleAnimated = false
        }
    }

    if (visibleAnimated) {
        DisposableEffect(Unit) {
            ActiveEnhancedDialogCount.intValue++
            ActiveEnhancedDialogIds.add(dialogId)
            onDispose {
                ActiveEnhancedDialogCount.intValue = (ActiveEnhancedDialogCount.intValue - 1).coerceAtLeast(0)
                ActiveEnhancedDialogIds.remove(dialogId)
                ActiveNativeDialogFrames.remove(dialogId)
                visibleAnimated = false
            }
        }
        val ownsNativeBackdrop = ActiveEnhancedDialogIds.firstOrNull() == dialogId
        val dialogCount = ActiveEnhancedDialogCount.intValue
        val nativeScrimAlpha by
            animateFloatAsState(
                targetValue = if (dialogCount > 1) 0.07f else 0.16f,
                animationSpec = tween(180, easing = IosDialogEnterEasing),
                label = "native dialog scrim alpha",
            )
        FullscreenDialog(onDismiss = onDismissRequest, visible = visible) {
            CompositionLocalProvider(
                LocalEnhancedDialogDepth provides dialogDepth + 1,
            ) {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    var animateIn by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        if (nativeComponentsEnabled) delay(34.milliseconds)
                        animateIn = true
                    }
                    if (nativeComponentsEnabled && nativeDialogBackdrop != null && ownsNativeBackdrop) {
                        nativeDialogBackdrop(
                            Modifier.fillMaxSize(),
                            ActiveEnhancedDialogIds
                                .mapNotNull(ActiveNativeDialogFrames::get)
                                .filter(Rect::isValidNativeDialogFrame),
                        )
                    }
                    EnhancedAnimatedVisibility(
                        visible = animateIn && visible,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        val alpha = (if (nativeComponentsEnabled) nativeScrimAlpha else 0.5f) * animatedScale
                        Box(
                            modifier =
                                Modifier
                                    .pointerInput(Unit) {
                                        detectTapGestures {
                                            onDismissRequest.invoke()
                                            vibrator.enhancedVibrateN(EnhancedVibrations.QUICK_FALL)
                                        }
                                    }.then(
                                        if (nativeComponentsEnabled) {
                                            Modifier.background(MaterialTheme.colorScheme.scrim.copy(alpha = alpha))
                                        } else {
                                            Modifier.enhancedHazeEffect(
                                                AppHazeState.current.value,
                                                MaterialTheme.colorScheme.scrim,
                                                fallbackAlpha = alpha,
                                            )
                                        },
                                    ).fillMaxSize(),
                        )
                    }
                    val dialogContentModifier =
                        modifier
                            .safeDrawingPadding()
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                            .then(
                                if (nativeComponentsEnabled) {
                                    Modifier.pointerInput(Unit) {
                                        awaitPointerEventScope {
                                            while (true) {
                                                awaitPointerEvent(PointerEventPass.Final)
                                                    .changes
                                                    .filterNot { it.isConsumed }
                                                    .forEach { it.consume() }
                                            }
                                        }
                                    }
                                } else {
                                    Modifier
                                },
                            )
                    if (nativeComponentsEnabled) {
                        val hiddenPanelOffset =
                            if (constraints.hasBoundedHeight) {
                                maxHeight + 48.dp
                            } else {
                                48.dp
                            }
                        val panelOffset by
                            animateDpAsState(
                                targetValue = if (animateIn && visible) 0.dp else hiddenPanelOffset,
                                animationSpec =
                                    tween(
                                        durationMillis = if (visible) 480 else 300,
                                        easing = if (visible) IosDialogEnterEasing else IosDialogExitEasing,
                                    ),
                                label = "native dialog vertical offset",
                            )
                        CompositionLocalProvider(
                            LocalNativeDialogBoundsReporter provides
                                { bounds ->
                                    val frame =
                                        Rect(
                                            left = bounds.left / density,
                                            top = bounds.top / density,
                                            right = bounds.right / density,
                                            bottom = bounds.bottom / density,
                                        )
                                    if (frame.isValidNativeDialogFrame()) {
                                        ActiveNativeDialogFrames[dialogId] = frame
                                    }
                                },
                        ) {
                            Box(
                                modifier = dialogContentModifier.offset(y = panelOffset),
                                contentAlignment = Alignment.Center,
                                content = content,
                            )
                        }
                    } else {
                        EnhancedAnimatedVisibility(
                            visible = animateIn && visible,
                            enter =
                                fadeIn(tween(300)) +
                                    scaleIn(
                                        initialScale = .8f,
                                        animationSpec =
                                            spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessMediumLow,
                                            ),
                                    ),
                            exit =
                                fadeOut(tween(300)) +
                                    scaleOut(
                                        targetScale = .8f,
                                        animationSpec =
                                            spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessMediumLow,
                                            ),
                                    ),
                            modifier = Modifier.scale(animatedScale),
                        ) {
                            Box(
                                modifier = dialogContentModifier,
                                contentAlignment = Alignment.Center,
                                content = content,
                            )
                        }
                    }
                }
            }
            @Suppress("DEPRECATION")
            PredictiveBackHandler(enabled = visible) { progress ->
                try {
                    progress.collect { event ->
                        if (event.progress <= 0.05f) {
                            scale = 1f
                        }
                        scale = (1f - event.progress * 1.5f).coerceAtLeast(0.75f)
                    }
                    onDismissRequest()
                    delay(400.milliseconds)
                    scale = 1f
                } catch (_: Exception) {
                    scale = 1f
                }
            }
        }
    }
}

@Composable
private fun EnhancedAlertDialogContent(
    buttons: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)?,
    title: (@Composable () -> Unit)?,
    text: @Composable (() -> Unit)?,
    shape: Shape,
    containerColor: Color,
    shadowElevation: Dp,
    buttonContentColor: Color,
    iconContentColor: Color,
    titleContentColor: Color,
    textContentColor: Color,
) {
    val blurEnabled = LocalBlurEnabled.current
    val nativeDialogBoundsReporter = LocalNativeDialogBoundsReporter.current
    val nativeDialogBackdrop = LocalNativeDialogBackdrop.current
    val nativeComponentsEnabled = LocalNativeComponentsEnabled.current.value
    val useNativeDialogBackground = nativeComponentsEnabled && nativeDialogBackdrop != null

    val dialogContent: @Composable () -> Unit = {
        Box {
            if (useNativeDialogBackground) {
                Box(Modifier.matchParentSize().background(containerColor.copy(alpha = 0.14f)))
            }
            Column(modifier = Modifier.padding(DialogPadding)) {
                icon?.let {
                    CompositionLocalProvider(LocalContentColor provides iconContentColor) {
                        Box(
                            Modifier
                                .padding(IconPadding)
                                .align(Alignment.CenterHorizontally),
                        ) {
                            icon()
                        }
                    }
                }
                title?.let {
                    ProvideContentColorTextStyle(
                        contentColor = titleContentColor,
                        textStyle = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center),
                    ) {
                        Box(
                            Modifier
                                .padding(TitlePadding)
                                .align(
                                    if (icon == null) {
                                        Alignment.Start
                                    } else {
                                        Alignment.CenterHorizontally
                                    },
                                ),
                        ) {
                            title()
                        }
                    }
                }
                text?.let {
                    val textStyle = MaterialTheme.typography.bodyMedium
                    ProvideContentColorTextStyle(
                        contentColor = textContentColor,
                        textStyle = textStyle,
                    ) {
                        Box(
                            Modifier
                                .weight(weight = 1f, fill = false)
                                .padding(TextPadding)
                                .align(Alignment.Start),
                        ) {
                            text()
                        }
                    }
                }
                Box(modifier = Modifier.align(Alignment.End)) {
                    val textStyle = MaterialTheme.typography.labelLarge
                    ProvideContentColorTextStyle(
                        contentColor = buttonContentColor,
                        textStyle = textStyle,
                        content = buttons,
                    )
                }
            }
        }
    }

    if (useNativeDialogBackground) {
        Surface(
            modifier =
                modifier
                    .drawBehind {
                        drawPath(
                            path =
                                Path().apply {
                                    addOutline(shape.createOutline(size, layoutDirection, this@drawBehind))
                                },
                            color = Color.Transparent,
                            blendMode = BlendMode.Clear,
                        )
                    }.then(
                        if (nativeDialogBoundsReporter != null) {
                            Modifier.onGloballyPositioned { coordinates ->
                                nativeDialogBoundsReporter(coordinates.boundsInRoot())
                            }
                        } else {
                            Modifier
                        },
                    ),
            shape = shape,
            color = Color.Transparent,
            content = dialogContent,
        )
    } else {
        Surface(
            modifier = modifier.clippedShadow(shadowElevation, shape),
            shape = shape,
            color = containerColor.copy(if (blurEnabled.value) 0.7f else 1f),
            content = dialogContent,
        )
    }
}

@Composable
fun ProvideContentColorTextStyle(
    contentColor: Color,
    textStyle: TextStyle,
    content: @Composable () -> Unit,
) {
    val mergedStyle = LocalTextStyle.current.merge(textStyle)
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalTextStyle provides mergedStyle,
        content = content,
    )
}

private val DialogMinWidth = 280.dp
private val DialogMaxWidth = 560.dp

private val ButtonsHorizontalSpacing = 8.dp
private val ButtonsVerticalSpacing = 12.dp

// Paddings for each of the dialog's parts.
private val DialogPadding = PaddingValues(all = 24.dp)
private val IconPadding = PaddingValues(bottom = 16.dp)
private val TitlePadding = PaddingValues(bottom = 16.dp)
private val TextPadding = PaddingValues(bottom = 24.dp)

private val ShadowElevation = 8.dp
