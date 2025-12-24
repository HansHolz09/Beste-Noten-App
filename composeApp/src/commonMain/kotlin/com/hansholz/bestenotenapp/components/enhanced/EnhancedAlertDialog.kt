package components.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.PredictiveBackHandler
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.components.FullscreenDialog
import com.hansholz.bestenotenapp.components.enhanced.enhancedHazeEffect
import com.hansholz.bestenotenapp.main.AppHazeState
import com.hansholz.bestenotenapp.main.Platform
import com.hansholz.bestenotenapp.main.getPlatform
import com.hansholz.bestenotenapp.theme.LocalBlurEnabled
import kotlinx.coroutines.delay

@Composable
fun EnhancedAlertDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    maxWidth: Dp? = null,
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
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
) {
    val hapticFeedback = LocalHapticFeedback.current

    BasicEnhancedAlertDialog(
        visible = visible,
        onDismissRequest = {
            onDismissRequest()
            hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
        },
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
                tonalElevation = tonalElevation,
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
                            minWidth = DialogMinWidth,
                            maxWidth = maxWidth ?: DialogMaxWidth,
                        ).then(if (maxWidth == Dp.Unspecified && getPlatform() == Platform.DESKTOP) Modifier.padding(top = 30.dp) else Modifier)
                        .then(Modifier.semantics { paneTitle = "Dialog" })
                        .animateContentSize(),
            )
        },
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BasicEnhancedAlertDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    var visibleAnimated by remember { mutableStateOf(false) }

    var scale by remember {
        mutableFloatStateOf(1f)
    }
    val animatedScale by animateFloatAsState(scale)

    LaunchedEffect(visible) {
        if (visible) {
            scale = 1f
            visibleAnimated = true
        } else if (getPlatform() != Platform.ANDROID) {
            delay(200)
            visibleAnimated = false
        }
    }

    if (visibleAnimated) {
        FullscreenDialog {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                var animateIn by rememberSaveable { mutableStateOf(false) }
                LaunchedEffect(Unit) { animateIn = true }
                AnimatedVisibility(
                    visible = animateIn && visible,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    val alpha = 0.5f * animatedScale

                    Box(
                        modifier =
                            Modifier
                                .pointerInput(Unit) { detectTapGestures { onDismissRequest.invoke() } }
                                .enhancedHazeEffect(AppHazeState.current.value, MaterialTheme.colorScheme.scrim, fallbackAlpha = alpha)
                                .fillMaxSize(),
                    )
                }
                AnimatedVisibility(
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
                        modifier =
                            modifier
                                .safeDrawingPadding()
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                        content = content,
                    )
                }
            }

            DisposableEffect(Unit) {
                onDispose {
                    visibleAnimated = false
                }
            }

            PredictiveBackHandler(enabled = visible) { progress ->
                try {
                    progress.collect { event ->
                        if (event.progress <= 0.05f) {
                            scale = 1f
                        }
                        scale = (1f - event.progress * 1.5f).coerceAtLeast(0.75f)
                    }
                    onDismissRequest()
                    delay(400)
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
    tonalElevation: Dp,
    buttonContentColor: Color,
    iconContentColor: Color,
    titleContentColor: Color,
    textContentColor: Color,
) {
    val blurEnabled = LocalBlurEnabled.current

    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor.copy(if (blurEnabled.value) 0.7f else 1f),
        tonalElevation = tonalElevation,
    ) {
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
                        // Align the title to the center when an icon is present.
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
