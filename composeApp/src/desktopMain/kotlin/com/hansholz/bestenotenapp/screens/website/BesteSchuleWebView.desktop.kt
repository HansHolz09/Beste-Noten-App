package com.hansholz.bestenotenapp.screens.website

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import com.hansholz.bestenotenapp.main.ExactPlatform
import com.hansholz.bestenotenapp.main.getExactPlatform
import dev.nucleusframework.webview.jsbridge.IJsMessageHandler
import dev.nucleusframework.webview.jsbridge.JsMessage
import dev.nucleusframework.webview.jsbridge.WebViewJsBridge
import dev.nucleusframework.webview.request.RequestInterceptor
import dev.nucleusframework.webview.request.WebRequest
import dev.nucleusframework.webview.request.WebRequestInterceptResult
import dev.nucleusframework.webview.web.NativeWebView
import dev.nucleusframework.webview.web.WebContent
import dev.nucleusframework.webview.web.WebView
import dev.nucleusframework.webview.web.WebViewNavigator
import dev.nucleusframework.webview.web.WebViewState
import dev.nucleusframework.webview.web.rememberWebViewNavigator
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import kotlin.time.Duration.Companion.seconds

internal actual val isBesteSchuleWebViewSupported = true

@Composable
internal actual fun isBesteSchuleWebViewTransparent(): Boolean = false

private const val JS_BRIDGE_NAME = "besteNotenAppNative"
private val MESSAGE_TYPES = setOf("getToken", "reauthenticate", "getTheme", "rendered", "ready", "backdropShown", "health", "problem", "openExternal", "close")

@Composable
internal actual fun BesteSchuleWebView(
    url: String,
    bridge: BesteSchuleWebBridge,
    transparent: Boolean,
    backgroundColor: Color,
    alpha: Float,
    modifier: Modifier,
) {
    val state =
        remember {
            WebViewState(WebContent.Url(url)).apply {
                webSettings.customUserAgentString = desktopUserAgent()
                webSettings.desktopWebSettings.apply {
                    initScript =
                        bridge.script(
                            "desktop",
                            "window.$JS_BRIDGE_NAME.callNative(type, JSON.stringify({ id: id, arg: arg }));",
                            fileUploads = getExactPlatform() != ExactPlatform.MACOS,
                        )
                    incognito = true
                    this.transparent = transparent
                }
                webSettings.backgroundColor = if (transparent) Color.Transparent else backgroundColor
            }
        }
    val interceptor =
        remember(bridge) {
            object : RequestInterceptor {
                override fun onInterceptUrlRequest(
                    request: WebRequest,
                    navigator: WebViewNavigator,
                ): WebRequestInterceptResult {
                    if (BesteSchuleWebsite.isInternalUrl(request.url)) return WebRequestInterceptResult.Allow
                    bridge.openExternal(request.url)
                    return WebRequestInterceptResult.Reject
                }
            }
        }
    val navigator = rememberWebViewNavigator(requestInterceptor = interceptor)
    val jsBridge = remember(navigator) { WebViewJsBridge(navigator, JS_BRIDGE_NAME) }

    DisposableEffect(jsBridge, bridge) {
        val handlers =
            MESSAGE_TYPES.map { type ->
                object : IJsMessageHandler {
                    override fun methodName() = type

                    override fun handle(
                        message: JsMessage,
                        navigator: WebViewNavigator?,
                        callback: (String) -> Unit,
                    ) {
                        val params = runCatching { Json.parseToJsonElement(message.params).jsonObject }.getOrNull() ?: return
                        val id = params["id"]?.jsonPrimitive?.content ?: return
                        val arg = params["arg"]?.jsonPrimitive?.content.orEmpty()
                        bridge.handle(type, id, arg) { script ->
                            if (BesteSchuleWebsite.isInternalUrl(state.lastLoadedUrl)) navigator?.evaluateJavaScript(script)
                        }
                    }
                }
            }
        handlers.forEach(jsBridge::register)
        onDispose { handlers.forEach(jsBridge::unregister) }
    }

    DisposableEffect(bridge, navigator) {
        bridge.attach { js -> if (BesteSchuleWebsite.isInternalUrl(state.lastLoadedUrl)) navigator.evaluateJavaScript(js) }
        onDispose { bridge.attach(null) }
    }

    var hasEngine by remember { mutableStateOf(false) }
    LaunchedEffect(bridge) {
        delay(2.seconds)
        if (!hasEngine) bridge.unavailableReason = missingEngineMessage()
    }

    WebView(
        state = state,
        modifier = modifier,
        navigator = navigator,
        webViewJsBridge = jsBridge,
        onCreated = { nativeWebView ->
            hasEngine = nativeWebView::class != NativeWebView::class
            if (hasEngine) bridge.unavailableReason = null
        },
    )
}

private fun missingEngineMessage() =
    when (getExactPlatform()) {
        ExactPlatform.WINDOWS -> "Zum Anzeigen der Webseite wird die Microsoft Edge WebView2 Runtime benötigt. Installiere sie oder öffne beste.schule im Browser."
        ExactPlatform.LINUX -> "Zum Anzeigen der Webseite wird WebKitGTK 4.1 benötigt (z.B. das Paket libwebkit2gtk-4.1-0). Installiere es oder öffne beste.schule im Browser."
        else -> "Die Webseite kann auf diesem System nicht in der App angezeigt werden. Öffne beste.schule stattdessen im Browser."
    }

private fun desktopUserAgent() =
    when (getExactPlatform()) {
        ExactPlatform.MACOS -> "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/26.0 Safari/605.1.15"
        ExactPlatform.WINDOWS -> "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0"
        else -> "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/26.0 Safari/605.1.15"
    } + " ${BesteSchuleWebsite.USER_AGENT_TOKEN}"

internal actual fun encodeJpeg(image: ImageBitmap) = Image.makeFromBitmap(image.asSkiaBitmap()).encodeToData(EncodedImageFormat.JPEG, 85)?.bytes
