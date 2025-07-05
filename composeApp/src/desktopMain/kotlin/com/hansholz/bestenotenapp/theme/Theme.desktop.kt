package com.hansholz.bestenotenapp.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme
import org.jetbrains.skiko.hostOs
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
internal actual fun SystemAppearance(
    isDark: Boolean,
    customColorScheme: @Composable (ColorScheme?) -> Unit,
) {
    var color: Color? = null

    try {
        if (hostOs.isWindows) {
            val process = ProcessBuilder("reg", "query", "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\DWM", "/v", "AccentColor").start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.use { it.readText() }

            val regex = Regex("AccentColor\\s+REG_DWORD\\s+0x([a-fA-F0-9]+)")
            val matchResult = regex.find(output)
            val colorHex = matchResult?.groups?.get(1)?.value

            val accentColor = colorHex?.toLong(16)?.toInt()

            val alpha = (accentColor!! shr 24) and 0xFF
            val blue = (accentColor shr 16) and 0xFF
            val green = (accentColor shr 8) and 0xFF
            val red = accentColor and 0xFF

            color = Color(red, green, blue, alpha)
        } else if (hostOs.isMacOS) {
            val accentColorMap =
                mapOf(
                    -1 to Color.Gray,
                    0 to Color(0xFF3B30),
                    1 to Color(0xFF9500),
                    2 to Color(0xFFCC00),
                    3 to Color(0x34C759),
                    4 to Color(0x007AFF),
                    5 to Color(0x5856D6),
                    6 to Color(0xAF52DE),
                )

            val process =
                ProcessBuilder("defaults", "read", "-g", "AppleAccentColor")
                    .redirectErrorStream(true)
                    .start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.use { it.readText() }

            val index = output.trimIndent().toIntOrNull()
            if (index != null) {
                color = accentColorMap[index]
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    customColorScheme(
        if (color != null) {
            rememberDynamicColorScheme(
                seedColor = color,
                isDark = isDark,
                specVersion = ColorSpec.SpecVersion.SPEC_2025,
            )
        } else {
            null
        },
    )
}