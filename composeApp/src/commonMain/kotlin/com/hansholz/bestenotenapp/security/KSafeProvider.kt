package com.hansholz.bestenotenapp.security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.KSafeWriteMode
import eu.anifantakis.lib.ksafe.compose.mutableStateOf

@Stable
class KSafeProvider(
    val kSafe: KSafe,
) {
    inline fun <reified T> get(
        key: String,
        defaultValue: T,
    ): T = kSafe.getDirect(key, defaultValue)

    inline fun <reified T> put(
        key: String,
        value: T,
    ) = kSafe.putDirect(key, value, KSafeWriteMode.Plain)

    inline fun <reified T> putSecure(
        key: String,
        value: T,
    ) = kSafe.putDirect(key, value, KSafeWriteMode.Encrypted())

    inline fun <reified T> storedMutableStateOf(
        defaultValue: T,
        key: String? = null,
    ) = kSafe.mutableStateOf(defaultValue, key, KSafeWriteMode.Plain)

    inline fun <reified T> secureMutableStateOf(
        defaultValue: T,
        key: String? = null,
    ) = kSafe.mutableStateOf(defaultValue, key, KSafeWriteMode.Encrypted())
}

inline fun <R> kSafeProvider(
    kSafe: KSafe,
    block: KSafeProvider.() -> R,
): R = KSafeProvider(kSafe).run(block)

@Composable
fun <R> kSafeProviderCompose(
    kSafe: KSafe = remember { kSafe() },
    content: @Composable KSafeProvider.() -> R,
): R = remember(kSafe) { KSafeProvider(kSafe) }.content()
