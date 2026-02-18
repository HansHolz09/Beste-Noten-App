package com.hansholz.bestenotenapp.security

import eu.anifantakis.lib.ksafe.KSafe
import io.github.kdroidfilter.nucleus.aot.runtime.AotRuntime

val kSafe = KSafe(if (AotRuntime.isTraining()) "training" else null)

actual fun kSafe(): KSafe = kSafe
