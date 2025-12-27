package com.hansholz.bestenotenapp.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.cache
import androidx.compose.runtime.currentComposer

@Composable
inline fun <T> tryRemember(
    vararg keys: Any?,
    crossinline calculation: @DisallowComposableCalls () -> T,
): T? {
    var invalid = false
    for (key in keys) invalid = invalid or currentComposer.changed(key)
    return currentComposer.cache(
        invalid,
        {
            try {
                calculation()
            } catch (_: Exception) {
                null
            }
        },
    )
}
