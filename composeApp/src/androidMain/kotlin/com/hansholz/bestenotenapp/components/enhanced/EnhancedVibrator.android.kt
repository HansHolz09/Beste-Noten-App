package com.hansholz.bestenotenapp.components.enhanced

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.context

private val richHapticsSupported by lazy {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
        FileKit.context.getSystemService(Vibrator::class.java)?.areAllPrimitivesSupported(
            VibrationEffect.Composition.PRIMITIVE_CLICK,
            VibrationEffect.Composition.PRIMITIVE_TICK,
        ) == true
}

actual fun hapticsEnabledByDefault(): Boolean = richHapticsSupported
