package com.hansholz.bestenotenapp.security

import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.compose.mutableStateOf

class KSafeProvider(
    val kSafe: KSafe,
) {
    inline fun <reified T> get(
        key: String,
        defaultValue: T,
    ): T = kSafe.getDirect(key, defaultValue, false)

    inline fun <reified T> put(
        key: String,
        value: T,
    ) = kSafe.putDirect(key, value, false)

    inline fun <reified T> getSecure(
        key: String,
        defaultValue: T,
    ): T = kSafe.getDirect(key, defaultValue)

    inline fun <reified T> putSecure(
        key: String,
        value: T,
    ) = kSafe.putDirect(key, value)

    inline fun <reified T> storedMutableStateOf(
        defaultValue: T,
        key: String? = null,
    ) = kSafe.mutableStateOf(defaultValue, key, false)

    inline fun <reified T> secureMutableStateOf(
        defaultValue: T,
        key: String? = null,
    ) = kSafe.mutableStateOf(defaultValue, key)
}

inline fun <R> kSafeProvider(
    kSafe: KSafe,
    block: KSafeProvider.() -> R,
): R = KSafeProvider(kSafe).run(block)
