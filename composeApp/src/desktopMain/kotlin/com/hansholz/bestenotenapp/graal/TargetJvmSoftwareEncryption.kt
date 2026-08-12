package com.hansholz.bestenotenapp.graal

import com.oracle.svm.core.annotate.Substitute
import com.oracle.svm.core.annotate.TargetClass
import kotlin.coroutines.Continuation

/**
 * KSafe's legacy OS-vault migration is unreachable because the desktop app explicitly uses the
 * software vault. GraalVM 25.0.4 cannot parse the coroutine's synchronized loop, so omit that
 * unreachable implementation from the native image.
 */
@TargetClass(className = "eu.anifantakis.lib.ksafe.internal.JvmSoftwareEncryption")
private class TargetJvmSoftwareEncryption {
    @Substitute
    fun migrateLegacyKeysSuspend(continuation: Continuation<Unit>): Any = Unit
}
