package com.hansholz.bestenotenapp.components.enhanced

import com.hansholz.bestenotenapp.main.Platform
import com.hansholz.bestenotenapp.main.getPlatform
import com.hansholz.bestenotenapp.security.kSafe
import top.ltfan.multihaptic.DelayType
import top.ltfan.multihaptic.HapticEffect
import top.ltfan.multihaptic.PrimitiveType
import top.ltfan.multihaptic.vibrator.Vibrator
import kotlin.time.Duration.Companion.milliseconds

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
}

fun Vibrator.enhancedVibrate(
    vibration: EnhancedVibrations,
    forceVibration: Boolean = false,
) {
    if (kSafe().getDirect("hapticsEnabled", listOf(Platform.ANDROID, Platform.IOS).contains(getPlatform())) || forceVibration) {
        vibrate(
            HapticEffect {
                when (vibration) {
                    CLICK -> predefined(PrimitiveType.Click, 1f)
                    THUD -> predefined(PrimitiveType.Thud, 1f)
                    SPIN -> predefined(PrimitiveType.Spin, 1f)
                    QUICK_RISE -> predefined(PrimitiveType.QuickRise, 1f)
                    SLOW_RISE -> predefined(PrimitiveType.SlowRise, 1f)
                    QUICK_FALL -> predefined(PrimitiveType.QuickFall, 1f)
                    TICK -> predefined(PrimitiveType.Tick, 1f)
                    LOW_TICK -> predefined(PrimitiveType.LowTick, 1f)
                    PAGE_SWIPE -> {
                        predefined(PrimitiveType.Click, 0.5f)
                        predefined(PrimitiveType.Tick, 0.5f, 20.milliseconds, DelayType.RelativeStartOffset)
                        predefined(PrimitiveType.Thud, 1f, 80.milliseconds, DelayType.RelativeStartOffset)
                    }
                    TOGGLE_ON -> {
                        predefined(PrimitiveType.Click, 0.5f)
                        predefined(PrimitiveType.Spin, 1f)
                        predefined(PrimitiveType.Tick, 1f)
                    }
                    TOGGLE_OFF -> {
                        predefined(PrimitiveType.Click, 0.75f)
                        predefined(PrimitiveType.QuickFall, 0.25f, 20.milliseconds)
                    }
                    EXPLOSION -> {
                        predefined(PrimitiveType.Click, 1f)
                        predefined(PrimitiveType.QuickFall, 1f)
                        repeat(20) {
                            predefined(PrimitiveType.Spin, 0.1f)
                        }
                    }
                    LOGO_RAIN -> {
                        repeat(33) {
                            predefined(PrimitiveType.QuickRise, 0.25f)
                        }
                    }
                }
            },
        )
    }
}
