package com.hansholz.bestenotenapp.screens.website

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.ktor.http.Url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

internal object BesteSchuleWebsite {
    const val ORIGIN = "https://beste.schule"
    const val START_URL = "$ORIGIN/home"
    const val USER_AGENT_TOKEN = "besteSchuleApp/2.0"

    var isAvailable by mutableStateOf(false)

    fun studentUrl(
        studentId: String,
        page: String,
    ) = "$ORIGIN/students/$studentId/$page"

    fun isInternalUrl(url: String?) =
        url != null &&
            runCatching { Url(url).let { it.protocol.name == "https" && it.host == "beste.schule" && it.port == 443 } }.getOrDefault(false)

    fun resolveScript(
        id: String,
        result: String?,
    ) = "window.__bnaResolve && window.__bnaResolve(${Json.encodeToString(id)}, ${result?.let { Json.encodeToString(it) } ?: "null"});"
}

internal class BesteSchuleWebBridge(
    private val scope: CoroutineScope,
    private val tokenProvider: suspend () -> String?,
    private val onOpenExternal: (String) -> Unit,
    private val onClose: () -> Unit,
    theme: BesteSchuleWebTheme,
    private val assets: BesteSchuleWebAssets,
) {
    var theme = theme
        private set
    var isRendered by mutableStateOf(false)
    var isReady by mutableStateOf(false)
    var isBackdropShown by mutableStateOf(false)
    var unavailableReason by mutableStateOf<String?>(null)
    var problem by mutableStateOf<String?>(null)
    var systemBottomInset by mutableStateOf(0f)

    private var evaluate: ((String) -> Unit)? = null
    private var backdrop: String? = null
    private var placement: BesteSchuleBackdropPlacement? = null

    fun script(
        platform: String,
        postMessage: String,
        fileUploads: Boolean = true,
    ) = assets.script(platform, postMessage, theme, fileUploads)

    fun attach(evaluate: ((String) -> Unit)?) {
        this.evaluate = evaluate
    }

    fun updateTheme(theme: BesteSchuleWebTheme) {
        if (theme == this.theme) return
        this.theme = theme
        evaluate?.invoke("window.__bnaApplyTheme && window.__bnaApplyTheme(${theme.toJson()});")
    }

    fun updateBackdrop(jpegBase64: String?) {
        if (jpegBase64 == backdrop) return
        backdrop = jpegBase64
        evaluate?.invoke(backdropScript(jpegBase64))
    }

    fun updateBackdropPlacement(placement: BesteSchuleBackdropPlacement) {
        if (placement == this.placement) return
        this.placement = placement
        evaluate?.invoke(placement.script())
    }

    fun handle(
        type: String,
        id: String,
        arg: String,
        respond: (String) -> Unit,
    ) {
        if (id.isEmpty() || id.length > 12 || !id.all(Char::isDigit)) return
        when (type) {
            "getToken", "reauthenticate" -> {
                scope.launch {
                    respond(BesteSchuleWebsite.resolveScript(id, runCatching { tokenProvider() }.getOrNull()))
                }
            }

            "getTheme" -> {
                respond(BesteSchuleWebsite.resolveScript(id, theme.toJson()))
                placement?.let { respond(it.script()) }
                backdrop?.let { respond(backdropScript(it)) }
            }

            "rendered" -> {
                isRendered = true
                respond(BesteSchuleWebsite.resolveScript(id, null))
            }

            "ready" -> {
                isRendered = true
                isReady = true
                respond(BesteSchuleWebsite.resolveScript(id, null))
            }

            "backdropShown" -> {
                isBackdropShown = true
                respond(BesteSchuleWebsite.resolveScript(id, null))
            }

            "health", "problem" -> {
                if (arg != "ok") problem = arg
                respond(BesteSchuleWebsite.resolveScript(id, null))
            }

            "openExternal" -> {
                openExternal(arg)
                respond(BesteSchuleWebsite.resolveScript(id, null))
            }

            "close" -> {
                respond(BesteSchuleWebsite.resolveScript(id, null))
                onClose()
            }
        }
    }

    fun openExternal(url: String) {
        if (url.substringBefore(':', "").lowercase() in setOf("https", "http", "mailto")) runCatching { onOpenExternal(url) }
    }

    private fun backdropScript(jpegBase64: String?) = "window.__bnaSetBackdrop && window.__bnaSetBackdrop(${jpegBase64?.let { "'$it'" } ?: "null"});"

    private fun BesteSchuleBackdropPlacement.script() = "window.__bnaPlaceBackdrop && window.__bnaPlaceBackdrop($x, $y, $width, $height);"
}

internal data class BesteSchuleBackdropPlacement(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
)

internal data class BesteSchuleWebTheme(
    val css: String,
    val dark: Boolean,
) {
    fun toJson() = "{\"css\":${Json.encodeToString(css)},\"dark\":$dark}"
}

internal expect val isBesteSchuleWebViewSupported: Boolean

@Composable
internal expect fun isBesteSchuleWebViewTransparent(): Boolean

@Composable
internal expect fun BesteSchuleWebView(
    url: String,
    bridge: BesteSchuleWebBridge,
    transparent: Boolean,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
)
