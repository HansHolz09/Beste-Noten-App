package com.hansholz.bestenotenapp.components.enhanced

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme.motionScheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp

@Composable
fun EnhancedCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    val hapticFeedback = LocalHapticFeedback.current

    val strokeWidthPx = with(LocalDensity.current) { 2.5.dp.toPx() }
    val toggleableState = ToggleableState(checked)

    CheckboxImpl(
        enabled = enabled,
        value = toggleableState,
        modifier =
            modifier
                .minimumInteractiveComponentSize()
                .triStateToggleable(
                    state = toggleableState,
                    onClick = {
                        val newState = !checked
                        onCheckedChange(newState)
                        hapticFeedback.performHapticFeedback(
                            if (newState) {
                                HapticFeedbackType.ToggleOn
                            } else {
                                HapticFeedbackType.ToggleOff
                            },
                        )
                    },
                    enabled = enabled,
                    role = Role.Checkbox,
                    interactionSource = interactionSource,
                    indication = ripple(bounded = false, radius = 20.dp),
                ).padding(2.dp),
        colors = colors,
        checkmarkStroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
        outlineStroke = Stroke(width = strokeWidthPx),
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CheckboxImpl(
    enabled: Boolean,
    value: ToggleableState,
    modifier: Modifier,
    colors: CheckboxColors,
    checkmarkStroke: Stroke,
    outlineStroke: Stroke,
) {
    val transition = updateTransition(value, label = "CheckboxTransition")
    val defaultAnimationSpec = motionScheme.defaultSpatialSpec<Float>()
    val checkDrawFraction =
        transition.animateFloat(
            label = "CheckDrawFraction",
            transitionSpec = {
                when {
                    initialState == ToggleableState.Off -> defaultAnimationSpec
                    targetState == ToggleableState.Off -> snap(delayMillis = 100)
                    else -> defaultAnimationSpec
                }
            },
        ) {
            when (it) {
                ToggleableState.On -> 1f
                ToggleableState.Off -> 0f
                ToggleableState.Indeterminate -> 1f
            }
        }

    val checkCenterGravitationShiftFraction =
        transition.animateFloat(
            label = "CheckCenterGravitationShiftFraction",
            transitionSpec = {
                when {
                    initialState == ToggleableState.Off -> snap()
                    targetState == ToggleableState.Off -> snap(delayMillis = 100)
                    else -> defaultAnimationSpec
                }
            },
        ) {
            when (it) {
                ToggleableState.On -> 0f
                ToggleableState.Off -> 0f
                ToggleableState.Indeterminate -> 1f
            }
        }
    val checkCache = remember { CheckDrawingCache() }
    val checkColor = colors.checkmarkColor(value)
    val boxColor = colors.boxColor(enabled, value)
    val borderColor = colors.borderColor(enabled, value)

    Canvas(modifier.wrapContentSize(Alignment.Center).requiredSize(20.dp)) {
        val strokeWidth = outlineStroke.width
        val radius = (size.minDimension - strokeWidth) / 1.5f

        drawCircle(
            color = boxColor.value,
            radius = radius,
            center = center,
        )

        drawCircle(
            color = borderColor.value,
            radius = radius,
            center = center,
            style = outlineStroke,
        )

        drawCheck(
            checkColor = checkColor.value,
            checkFraction = checkDrawFraction.value,
            crossCenterGravitation = checkCenterGravitationShiftFraction.value,
            stroke = checkmarkStroke,
            drawingCache = checkCache,
        )
    }
}

@Composable
private fun CheckboxColors.checkmarkColor(state: ToggleableState): State<Color> {
    val target =
        if (state == ToggleableState.Off) {
            uncheckedCheckmarkColor
        } else {
            checkedCheckmarkColor
        }

    return animateColorAsState(target, colorAnimationSpecForState(state))
}

@Composable
internal fun CheckboxColors.boxColor(
    enabled: Boolean,
    state: ToggleableState,
): State<Color> {
    val target =
        if (enabled) {
            when (state) {
                ToggleableState.On,
                ToggleableState.Indeterminate,
                -> checkedBoxColor
                ToggleableState.Off -> uncheckedBoxColor
            }
        } else {
            when (state) {
                ToggleableState.On -> disabledCheckedBoxColor
                ToggleableState.Indeterminate -> disabledIndeterminateBoxColor
                ToggleableState.Off -> disabledUncheckedBoxColor
            }
        }

    // If not enabled 'snap' to the disabled state, as there should be no animations between
    // enabled / disabled.
    return if (enabled) {
        animateColorAsState(target, colorAnimationSpecForState(state))
    } else {
        rememberUpdatedState(target)
    }
}

@Composable
private fun CheckboxColors.borderColor(
    enabled: Boolean,
    state: ToggleableState,
): State<Color> {
    val target =
        if (enabled) {
            when (state) {
                ToggleableState.On,
                ToggleableState.Indeterminate,
                -> checkedBorderColor
                ToggleableState.Off -> uncheckedBorderColor
            }
        } else {
            when (state) {
                ToggleableState.Indeterminate -> disabledIndeterminateBorderColor
                ToggleableState.On -> disabledBorderColor
                ToggleableState.Off -> disabledUncheckedBorderColor
            }
        }

    // If not enabled 'snap' to the disabled state, as there should be no animations between
    // enabled / disabled.
    return if (enabled) {
        animateColorAsState(target, colorAnimationSpecForState(state))
    } else {
        rememberUpdatedState(target)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun colorAnimationSpecForState(state: ToggleableState): AnimationSpec<Color> =
    if (state == ToggleableState.Off) {
        // Box out
        motionScheme.fastEffectsSpec()
    } else {
        // Box in
        motionScheme.defaultEffectsSpec()
    }

private fun DrawScope.drawCheck(
    checkColor: Color,
    checkFraction: Float,
    crossCenterGravitation: Float,
    stroke: Stroke,
    drawingCache: CheckDrawingCache,
) {
    val width = size.width
    val checkCrossX = 0.4f
    val checkCrossY = 0.7f
    val leftX = 0.2f
    val leftY = 0.5f
    val rightX = 0.8f
    val rightY = 0.3f

    val gravitatedCrossX = lerp(checkCrossX, 0.5f, crossCenterGravitation)
    val gravitatedCrossY = lerp(checkCrossY, 0.5f, crossCenterGravitation)
    // gravitate only Y for end to achieve center line
    val gravitatedLeftY = lerp(leftY, 0.5f, crossCenterGravitation)
    val gravitatedRightY = lerp(rightY, 0.5f, crossCenterGravitation)

    with(drawingCache) {
        checkPath.rewind()
        checkPath.moveTo(width * leftX, width * gravitatedLeftY)
        checkPath.lineTo(width * gravitatedCrossX, width * gravitatedCrossY)
        checkPath.lineTo(width * rightX, width * gravitatedRightY)
        pathMeasure.setPath(checkPath, false)
        pathToDraw.rewind()
        pathMeasure.getSegment(0f, pathMeasure.length * checkFraction, pathToDraw, true)
    }
    drawPath(drawingCache.pathToDraw, checkColor, style = stroke)
}

@Immutable
private class CheckDrawingCache(
    val checkPath: Path = Path(),
    val pathMeasure: PathMeasure = PathMeasure(),
    val pathToDraw: Path = Path(),
)
