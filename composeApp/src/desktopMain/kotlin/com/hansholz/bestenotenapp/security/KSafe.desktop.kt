package com.hansholz.bestenotenapp.security

import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.KSafeConfig
import io.github.kdroidfilter.nucleus.aot.runtime.AotRuntime
import io.github.kdroidfilter.nucleus.aot.runtime.ExecutableRuntime

val kSafe =
    KSafe(
        fileName = if (AotRuntime.isTraining()) "training" else "bna",
        config =
            KSafeConfig(
                appNamespace =
                    if (AotRuntime.isTraining()) {
                        "dev.hansholz.bna.training"
                    } else if (ExecutableRuntime.isDev()) {
                        "dev.hansholz.bna.dev"
                    } else {
                        "dev.hansholz.bna"
                    },
            ),
    )

actual fun kSafe(): KSafe = kSafe
