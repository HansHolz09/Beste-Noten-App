package com.hansholz.bestenotenapp.security

import dev.nucleusframework.core.runtime.ExecutableRuntime
import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.KSafeConfig

val kSafe =
    KSafe(
        fileName = "bna",
        config =
            KSafeConfig(
                appNamespace =
                    if (ExecutableRuntime.isDev()) {
                        "dev.hansholz.bna.dev"
                    } else {
                        "dev.hansholz.bna"
                    },
            ),
    )

actual fun kSafe(): KSafe = kSafe
