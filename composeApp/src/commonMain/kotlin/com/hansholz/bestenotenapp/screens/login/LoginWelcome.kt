package com.hansholz.bestenotenapp.screens.login

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import bestenotenapp.composeapp.generated.resources.Res
import bestenotenapp.composeapp.generated.resources.logo
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrateN
import kotlinx.coroutines.isActive
import org.jetbrains.compose.resources.painterResource
import top.ltfan.multihaptic.compose.rememberVibrator
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

@Composable
internal fun LoginWelcome(
    playIntro: Boolean,
    animationsEnabled: Boolean,
    onIntroCompleted: () -> Unit,
    onChromeAlphaChanged: (Float) -> Unit = {},
    content: @Composable BoxScope.(header: @Composable () -> Unit, formModifier: Modifier, introFinished: Boolean) -> Unit,
) {
    val startsWithIntro = remember { playIntro && animationsEnabled }
    var elapsed by remember { mutableFloatStateOf(if (startsWithIntro) 0f else INTRO_DURATION) }
    var completed by remember { mutableStateOf(!startsWithIntro) }
    val onCompleted by rememberUpdatedState(onIntroCompleted)
    val onChromeAlpha by rememberUpdatedState(onChromeAlphaChanged)
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val vibrator = rememberVibrator()
    var returnElapsed by remember { mutableStateOf<Float?>(null) }
    var returnOrigin by remember { mutableStateOf<Offset?>(null) }
    var motionElapsed by remember { mutableFloatStateOf(0f) }
    var headerCenter by remember { mutableStateOf<Offset?>(null) }
    var origin by remember { mutableStateOf(Offset.Zero) }
    var viewportSize by remember { mutableStateOf(IntSize.Zero) }
    var flights by remember { mutableStateOf(newLetterFlights()) }
    val textMeasurer = rememberTextMeasurer()
    val style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.onSurface)
    val glyphs = remember(textMeasurer, style) { WELCOME_TEXT.map { textMeasurer.measure(it.toString(), style) } }
    val textWidth = remember(glyphs) { glyphs.sumOf { it.size.width }.toFloat() }
    val sentenceWidth = with(LocalDensity.current) { (viewportSize.width - 32.dp.roundToPx()).coerceAtLeast(1) }
    val sentenceLayout =
        remember(textMeasurer, style, sentenceWidth) {
            val sentence =
                when {
                    textMeasurer.measure(WELCOME_TEXT, style).size.width <= sentenceWidth -> {
                        WELCOME_TEXT
                    }

                    textMeasurer.measure("Willkommen bei der", style).size.width <= sentenceWidth -> {
                        WELCOME_TEXT.replace(" Beste", "\nBeste")
                    }

                    else -> {
                        WELCOME_TEXT.replace("Willkommen ", "Willkommen\n").replace(" Beste", "\nBeste")
                    }
                }
            textMeasurer.measure(
                sentence,
                style = style.copy(textAlign = TextAlign.Center),
                constraints = Constraints(maxWidth = sentenceWidth),
            )
        }
    val sentencePositions =
        remember(sentenceLayout) {
            WELCOME_TEXT.indices.map { index ->
                sentenceLayout.getBoundingBox(index).center - Offset(sentenceLayout.size.width / 2f, 0f)
            }
        }

    LaunchedEffect(Unit) {
        if (playIntro && !startsWithIntro) onCompleted()
    }

    LaunchedEffect(animationsEnabled) {
        if (!animationsEnabled) {
            elapsed = INTRO_DURATION
            returnElapsed = null
            if (!completed) {
                completed = true
                onCompleted()
            }
            return@LaunchedEffect
        }
        var previous = withFrameNanos { it }
        var lastMotionHaptic = Float.NEGATIVE_INFINITY
        while (isActive) {
            val previousElapsed = elapsed
            withFrameNanos { now ->
                val delta = (now - previous) / 1_000_000_000f
                elapsed += delta
                motionElapsed += delta
                returnElapsed?.let { returning ->
                    val next = returning + delta
                    if (next >= RETURN_DURATION) {
                        elapsed = 0f
                        returnElapsed = null
                        flights = newLetterFlights()
                        lastMotionHaptic = Float.NEGATIVE_INFINITY
                    } else {
                        returnElapsed = next
                    }
                }
                previous = now
            }
            if (!completed && returnElapsed == null && elapsed - previousElapsed in 0f..0.15f) {
                val flyingLetters =
                    flights.indices.count { index ->
                        val start = 0.12f + flights[index].delay
                        !WELCOME_TEXT[index].isWhitespace() && elapsed >= start && elapsed < start + flights[index].duration
                    }
                val flightStart = 0.12f + flights.minOf { it.delay }
                val flightEnd = 0.12f + flights.maxOf { it.delay + it.duration }
                val flightEnvelope =
                    smoothProgress(elapsed, flightStart, 0.55f) *
                        (1f - smoothProgress(elapsed, flightEnd - 0.65f, 0.65f))
                val formingOrbit = elapsed in MORPH_START..(MORPH_START + 1.3f)
                val docking = elapsed in DOCK_START..(DOCK_START + 0.85f)
                val interval =
                    if (flyingLetters > 0) {
                        mix(0.20f, 0.10f, flightEnvelope)
                    } else if (formingOrbit) {
                        0.16f
                    } else {
                        0.22f
                    }
                if ((flyingLetters > 0 || formingOrbit || docking) && elapsed - lastMotionHaptic >= interval) {
                    vibrator.enhancedVibrateN(
                        if (flyingLetters > 0) EnhancedVibrations.WELCOME_LETTER else EnhancedVibrations.WELCOME_MOTION,
                        intensity = if (flyingLetters > 0) mix(0.25f, 0.85f, flightEnvelope) else 1f,
                    )
                    lastMotionHaptic = elapsed
                }
            }
            if (!completed && returnElapsed == null && elapsed >= INTRO_DURATION) {
                completed = true
                onCompleted()
            }
        }
    }

    val returning = returnElapsed
    val formAlpha =
        when {
            returning != null -> 1f - smoothProgress(returning, 0f, 0.35f)
            completed -> 1f
            else -> smoothProgress(elapsed, FORM_REVEAL_START, 0.65f)
        }
    SideEffect { onChromeAlpha(formAlpha) }
    DisposableEffect(Unit) {
        onDispose { onChromeAlpha(1f) }
    }
    val dock =
        when {
            returning != null -> 1f - smoothProgress(returning, 0.22f, 0.75f)
            completed -> 1f
            else -> smoothProgress(elapsed, DOCK_START, 0.85f)
        }
    val formModifier =
        Modifier
            .focusProperties { canFocus = completed }
            .graphicsLayer {
                alpha = formAlpha
                translationY = (1f - formAlpha) * 18.dp.toPx()
            }.then(if (completed) Modifier else Modifier.clearAndSetSemantics {})

    Box(
        Modifier
            .fillMaxSize()
            .clipToBounds()
            .onSizeChanged { viewportSize = it }
            .onGloballyPositioned { origin = it.positionInRoot() },
        contentAlignment = Alignment.Center,
    ) {
        content(
            {
                Box(
                    Modifier.size(225.dp).onGloballyPositioned {
                        headerCenter = it.positionInRoot() + Offset(it.size.width / 2f, it.size.height / 2f)
                    },
                )
            },
            formModifier,
            completed,
        )
        val target = headerCenter?.minus(origin)
        if (target != null) {
            Canvas(
                Modifier
                    .fillMaxSize()
                    .clearAndSetSemantics {},
            ) {
                val time = elapsed
                val destination = if (returning != null) returnOrigin ?: target else target
                val heroCenter = Offset(mix(center.x, destination.x, dock), mix(center.y, destination.y, dock))
                val radius = 100.dp.toPx()
                val orbitTime = (time - MORPH_START).coerceAtLeast(0f)
                val orbitDegrees =
                    if (!animationsEnabled) {
                        0f
                    } else {
                        -36f *
                            (
                                if (orbitTime < 1f) {
                                    orbitTime * orbitTime / 2f
                                } else {
                                    orbitTime - 0.5f
                                }
                            )
                    }
                val arcLength = radius * (315f * PI.toFloat() / 180f)
                val startDistance = ((arcLength - textWidth) / 2f).coerceAtLeast(0f)
                var distance = 0f
                glyphs.forEachIndexed { index, glyph ->
                    val width = glyph.size.width.toFloat()
                    val flight = flights[index]
                    val arrival = smoothProgress(time, 0.12f + flight.delay, flight.duration)
                    val morph = if (completed || returning != null) 1f else smoothProgress(time, MORPH_START + index * 0.008f, 1.05f)
                    val sentencePosition = heroCenter + Offset(0f, 100.dp.toPx()) + sentencePositions[index]
                    val angle = 110f + (startDistance + distance + width / 2f) / radius * 180f / PI.toFloat() + orbitDegrees
                    val radians = angle * PI.toFloat() / 180f
                    val ring = heroCenter + Offset(cos(radians) * radius, sin(radians) * radius)
                    val travel =
                        if (flight.outsideScreen) {
                            size.width + size.height + textWidth
                        } else {
                            (0.18f + flight.distance * 0.22f) * min(size.width, size.height)
                        }
                    val scatter = Offset(cos(flight.direction), sin(flight.direction)) * travel * (1f - arrival)
                    val position = Offset(mix(sentencePosition.x, ring.x, morph), mix(sentencePosition.y, ring.y, morph)) + scatter
                    val alpha = if (completed || flight.outsideScreen) 1f else progress(time, 0.12f + flight.delay, flight.duration * 0.65f)
                    withTransform({
                        translate(position.x, position.y)
                        rotate(mix(flight.rotation * (1f - arrival), angle + 90f, morph), pivot = Offset.Zero)
                    }) {
                        val textAlpha = if (returning != null) 1f - smoothProgress(returning, 0f, 0.35f) else 1f
                        drawText(glyph, topLeft = Offset(-width / 2f, -glyph.size.height / 2f), alpha = alpha * textAlpha)
                    }
                    distance += width
                }
            }
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = null,
                modifier =
                    Modifier
                        .size(150.dp)
                        .graphicsLayer {
                            val destination = if (returning != null) returnOrigin ?: target else target
                            val viewportCenter = Offset(viewportSize.width / 2f, viewportSize.height / 2f)
                            translationX = mix(0f, destination.x - viewportCenter.x, dock)
                            translationY = mix(0f, destination.y - viewportCenter.y, dock)
                            if (animationsEnabled) {
                                val breath = sin(motionElapsed * 2f)
                                scaleX = 1f + breath * 0.012f
                                scaleY = scaleX
                                rotationZ = sin(motionElapsed * 1.5f) * 1.5f
                            }
                        }.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = completed && animationsEnabled,
                            onClickLabel = "Begrüßung erneut abspielen",
                        ) {
                            vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                            focusManager.clearFocus(force = true)
                            keyboard?.hide()
                            returnOrigin = target
                            returnElapsed = 0f
                            completed = false
                        },
            )
        }
        if (!completed) {
            Box(
                Modifier
                    .fillMaxSize()
                    .semantics { contentDescription = WELCOME_TEXT }
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) awaitPointerEvent().changes.forEach { it.consume() }
                        }
                    },
            )
        }
    }
}

private data class LetterFlight(
    val direction: Float,
    val distance: Float,
    val delay: Float,
    val duration: Float,
    val rotation: Float,
    val outsideScreen: Boolean,
)

private fun newLetterFlights(): List<LetterFlight> =
    List(WELCOME_TEXT.length) { index ->
        LetterFlight(
            direction = Random.nextFloat() * 2f * PI.toFloat(),
            distance = Random.nextFloat(),
            delay = Random.nextFloat() * 0.45f,
            duration = 0.85f + Random.nextFloat() * 0.7f,
            rotation = Random.nextFloat() * 140f - 70f,
            outsideScreen = index % 3 != 0,
        )
    }

private fun progress(
    time: Float,
    start: Float,
    duration: Float,
): Float = ((time - start) / duration).coerceIn(0f, 1f)

private fun smoothProgress(
    time: Float,
    start: Float,
    duration: Float,
): Float = FastOutSlowInEasing.transform(progress(time, start, duration))

private fun mix(
    from: Float,
    to: Float,
    progress: Float,
): Float = from + (to - from) * progress

private const val WELCOME_TEXT = "Willkommen bei der Beste-Noten-App"
private const val INTRO_DURATION = 6.3f
private const val MORPH_START = 3.2f
private const val DOCK_START = 4.95f
private const val FORM_REVEAL_START = 5.65f
private const val RETURN_DURATION = 1.1f
