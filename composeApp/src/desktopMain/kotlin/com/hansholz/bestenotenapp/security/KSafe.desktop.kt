package com.hansholz.bestenotenapp.security

import dev.nucleusframework.aot.runtime.AotRuntime
import dev.nucleusframework.core.runtime.ExecutableRuntime
import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.KSafeConfig

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
