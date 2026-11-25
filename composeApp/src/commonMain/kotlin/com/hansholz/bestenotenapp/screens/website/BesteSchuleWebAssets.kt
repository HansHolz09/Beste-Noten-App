package com.hansholz.bestenotenapp.screens.website

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import bestenotenapp.composeapp.generated.resources.Res
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.math.roundToInt

internal class BesteSchuleWebAssets(
    private val bridgeJs: String,
    private val baseCss: String,
    private val designCss: String,
    private val focusedCss: String,
    private val fontsCss: String,
) {
    fun script(
        platform: String,
        postMessage: String,
        theme: BesteSchuleWebTheme,
        fileUploads: Boolean,
    ) = bridgeJs.fill(
        "origin" to BesteSchuleWebsite.ORIGIN,
        "platform" to platform,
        "postMessage" to postMessage,
        "theme" to theme.toJson(),
        "fonts" to Json.encodeToString(fontsCss),
        "fileUploads" to fileUploads.toString(),
    )

    fun theme(
        colors: ColorScheme,
        dark: Boolean,
        design: Boolean,
        transparent: Boolean,
        focused: Boolean,
        bottomInset: Float,
    ) = BesteSchuleWebTheme(
        css =
            buildString {
                append("html:not(#bna){--bna-inset-bottom:${(bottomInset * 10).roundToInt() / 10.0}px;")
                if (design) {
                    append("color-scheme:${if (dark) "dark" else "light"};")
                    append("--bna-page:${if (transparent) "transparent" else "rgb(${colors.background.rgb()})"};")
                    listOf(
                        "primary" to colors.primary,
                        "on-primary" to colors.onPrimary,
                        "primary-container" to colors.primaryContainer,
                        "on-primary-container" to colors.onPrimaryContainer,
                        "secondary" to colors.secondary,
                        "on-secondary" to colors.onSecondary,
                        "secondary-container" to colors.secondaryContainer,
                        "on-secondary-container" to colors.onSecondaryContainer,
                        "error" to colors.error,
                        "on-error" to colors.onError,
                        "error-container" to colors.errorContainer,
                        "on-error-container" to colors.onErrorContainer,
                        "surface" to colors.surface,
                        "on-surface" to colors.onSurface,
                        "on-surface-variant" to colors.onSurfaceVariant,
                        "surface-container" to colors.surfaceContainer,
                        "surface-container-high" to colors.surfaceContainerHigh,
                        "surface-container-highest" to colors.surfaceContainerHighest,
                        "outline" to colors.outline,
                        "outline-variant" to colors.outlineVariant,
                    ).forEach { (name, color) -> append("--bna-$name:${color.rgb()};") }
                }
                append("}")
                append(baseCss)
                if (design) {
                    append(designCss.fill("onPrimaryHex" to "%23" + colors.onPrimary.hex()))
                    if (focused) append(focusedCss)
                }
            },
        dark = dark,
    )
}

internal suspend fun loadBesteSchuleWebAssets(design: Boolean) =
    BesteSchuleWebAssets(
        bridgeJs = Res.readBytes("files/website/bridge.js").decodeToString(),
        baseCss = Res.readBytes("files/website/base.css").decodeToString(),
        designCss = Res.readBytes("files/website/design.css").decodeToString(),
        focusedCss = Res.readBytes("files/website/focused.css").decodeToString(),
        fontsCss =
            buildString {
                if (design) {
                    for (
                    (name, path) in
                    listOf(
                        "BNA Sniglet" to "font/Sniglet-Regular.ttf",
                        "BNA Keania One" to "font/KeaniaOne-Regular.ttf",
                        "BNA Schoolbell" to "font/Schoolbell-Regular.ttf",
                    )
                    ) {
                        append("@font-face{font-family:\"$name\";src:url(data:font/ttf;base64,${Base64.encode(Res.readBytes(path))}) format(\"truetype\");font-display:block;}")
                    }
                }
            },
    )

private fun String.fill(vararg values: Pair<String, String>) =
    Regex("\\{\\{(\\w+)\\}\\}").replace(this) { match -> values.firstOrNull { it.first == match.groupValues[1] }?.second ?: match.value }

private fun Color.channels() = listOf(red, green, blue).map { (it * 255).roundToInt().coerceIn(0, 255) }

private fun Color.rgb() = channels().joinToString(",")

private fun Color.hex() = channels().joinToString("") { it.toString(16).padStart(2, '0') }
