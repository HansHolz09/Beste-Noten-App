package com.hansholz.bestenotenapp.components.enhanced

import androidx.compose.runtime.Composable
import com.hansholz.bestenotenapp.main.Platform
import com.hansholz.bestenotenapp.main.getPlatform
import com.hansholz.bestenotenapp.security.KSafeProvider
import com.hansholz.bestenotenapp.security.kSafe
import com.hansholz.bestenotenapp.security.kSafeProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import top.ltfan.multihaptic.DelayType
import top.ltfan.multihaptic.HapticEffect
import top.ltfan.multihaptic.PrimitiveType
import top.ltfan.multihaptic.getVibrator
import top.ltfan.multihaptic.vibrator.Vibrator
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun rememberEnhancedVibrator(): Vibrator = EnhancedVibrator

fun configureEnhancedVibrator(config: Any) {
    EnhancedVibrator.configure(config)
}

internal object EnhancedVibrator : Vibrator {
    private var config: Any? = null

    private val scope =
        CoroutineScope(
            SupervisorJob() + Dispatchers.Main.immediate +
                CoroutineExceptionHandler { _, error -> error.printStackTrace() },
        )
    private val vibrator =
        lazy {
            runCatching { getVibrator(scope, config) }
                .onFailure { it.printStackTrace() }
                .getOrNull()
        }

    fun configure(config: Any) {
        this.config = config
    }

    override val isVibrationSupported: Boolean
        get() = vibrator.value?.isVibrationSupported ?: false

    override fun vibrate(effect: HapticEffect) {
        vibrator.value?.vibrate(effect)
    }

    override fun cancel() {
        if (vibrator.isInitialized()) vibrator.value?.cancel()
    }
}

enum class EnhancedVibrations {
    CLICK,
    THUD,
    SPIN,
    QUICK_RISE,
    SLOW_RISE,
    QUICK_FALL,
    TICK,
    LOW_TICK,
    PAGE_SWIPE,
    TOGGLE_ON,
    TOGGLE_OFF,
    EXPLOSION,
    LOGO_RAIN,
    WELCOME_LETTER,
    WELCOME_MOTION,
}

context(kSafeProvider: KSafeProvider)
fun Vibrator.enhancedVibrate(
    vibration: EnhancedVibrations,
    forceVibration: Boolean = false,
    intensity: Float = 1f,
) {
    if (kSafeProvider.get("hapticsEnabled", getPlatform() == Platform.ANDROID) || forceVibration) {
        executeVibration(vibration, intensity)
    }
}

fun Vibrator.enhancedVibrateN(
    vibration: EnhancedVibrations,
    forceVibration: Boolean = false,
    intensity: Float = 1f,
) {
    if (kSafeProvider(kSafe()) { get("hapticsEnabled", getPlatform() == Platform.ANDROID) } || forceVibration) {
        executeVibration(vibration, intensity)
    }
}

private fun Vibrator.executeVibration(
    vibration: EnhancedVibrations,
    intensity: Float,
) {
    val strength = intensity.coerceIn(0f, 1f)
    if (strength == 0f) return
    vibrate(
        HapticEffect {
            when (vibration) {
                CLICK -> {
                    predefined(PrimitiveType.Click, strength)
                }

                THUD -> {
                    predefined(PrimitiveType.Thud, strength)
                }

                SPIN -> {
                    predefined(PrimitiveType.Spin, strength)
                }

                QUICK_RISE -> {
                    predefined(PrimitiveType.QuickRise, strength)
                }

                SLOW_RISE -> {
                    predefined(PrimitiveType.SlowRise, strength)
                }

                QUICK_FALL -> {
                    predefined(PrimitiveType.QuickFall, strength)
                }

                TICK -> {
                    predefined(PrimitiveType.Tick, strength)
                }

                LOW_TICK -> {
                    predefined(PrimitiveType.LowTick, strength)
                }

                PAGE_SWIPE -> {
                    predefined(PrimitiveType.Click, 0.5f * strength)
                    predefined(PrimitiveType.Tick, 0.5f * strength, 20.milliseconds, DelayType.RelativeStartOffset)
                    predefined(PrimitiveType.Thud, strength, 80.milliseconds, DelayType.RelativeStartOffset)
                }

                TOGGLE_ON -> {
                    predefined(PrimitiveType.Click, 0.5f * strength)
                    predefined(PrimitiveType.Spin, strength)
                    predefined(PrimitiveType.Tick, strength)
                }

                TOGGLE_OFF -> {
                    predefined(PrimitiveType.Click, 0.75f * strength)
                    predefined(PrimitiveType.QuickFall, 0.25f * strength, 20.milliseconds)
                }

                EXPLOSION -> {
                    predefined(PrimitiveType.Click, strength)
                    predefined(PrimitiveType.QuickFall, strength)
                    repeat(20) {
                        predefined(PrimitiveType.Spin, 0.1f * strength)
                    }
                }

                WELCOME_LETTER -> {
                    predefined(PrimitiveType.QuickRise, 0.28f * strength)
                }

                WELCOME_MOTION -> {
                    predefined(PrimitiveType.Spin, 0.16f * strength)
                }

                LOGO_RAIN -> {
                    repeat(33) {
                        predefined(PrimitiveType.QuickRise, 0.25f * strength)
                    }
                }
            }
        },
    )
}
