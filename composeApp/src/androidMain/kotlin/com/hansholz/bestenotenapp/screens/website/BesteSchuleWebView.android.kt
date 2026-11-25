package com.hansholz.bestenotenapp.screens.website

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.MimeTypeMap
import android.webkit.RenderProcessGoneDetail
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal actual val isBesteSchuleWebViewSupported = true

@Composable
internal actual fun isBesteSchuleWebViewTransparent() = true

private const val NATIVE_INTERFACE = "BesteNotenAppNative"

@SuppressLint("SetJavaScriptEnabled")
@Composable
internal actual fun BesteSchuleWebView(
    url: String,
    bridge: BesteSchuleWebBridge,
    transparent: Boolean,
    backgroundColor: Color,
    alpha: Float,
    modifier: Modifier,
) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    var canGoBack by remember { mutableStateOf(false) }
    var renderProcessCrashes by remember { mutableIntStateOf(0) }
    var fileCallback by remember { mutableStateOf<ValueCallback<Array<Uri>>?>(null) }
    val filePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            fileCallback?.onReceiveValue(uris.toTypedArray())
            fileCallback = null
        }

    BackHandler(enabled = canGoBack) {
        webView?.goBack()
    }

    key(renderProcessCrashes) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    setBackgroundColor(if (transparent) android.graphics.Color.TRANSPARENT else backgroundColor.toArgb())
                    isVerticalScrollBarEnabled = false
                    isHorizontalScrollBarEnabled = false
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.userAgentString = "${settings.userAgentString} ${BesteSchuleWebsite.USER_AGENT_TOKEN}"

                    val script =
                        bridge.script(
                            "android",
                            "var n = window.$NATIVE_INTERFACE; var m = JSON.stringify({ type: type, id: id, arg: arg }); if (n.postMessage) n.postMessage(m); else n.post(m);",
                        )
                    if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER)) {
                        WebViewCompat.addWebMessageListener(this, NATIVE_INTERFACE, setOf(BesteSchuleWebsite.ORIGIN)) { view, message, _, isMainFrame, _ ->
                            if (isMainFrame) handleMessage(view, bridge, message.data)
                        }
                    } else {
                        addJavascriptInterface(NativeInterface(this, bridge), NATIVE_INTERFACE)
                    }
                    val hasDocumentStartScript = WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT)
                    if (hasDocumentStartScript) {
                        WebViewCompat.addDocumentStartJavaScript(this, script, setOf(BesteSchuleWebsite.ORIGIN))
                    }

                    webChromeClient =
                        object : WebChromeClient() {
                            override fun onShowFileChooser(
                                view: WebView,
                                callback: ValueCallback<Array<Uri>>,
                                params: FileChooserParams,
                            ): Boolean {
                                fileCallback?.onReceiveValue(null)
                                fileCallback = callback
                                val types =
                                    params.acceptTypes
                                        .flatMap { it.split(',') }
                                        .map { it.trim() }
                                        .filter { it.isNotEmpty() }
                                        .map { if (it.startsWith('.')) MimeTypeMap.getSingleton().getMimeTypeFromExtension(it.drop(1)) ?: "*/*" else it }
                                return runCatching { filePicker.launch(types.ifEmpty { listOf("*/*") }.toTypedArray()) }
                                    .onFailure {
                                        callback.onReceiveValue(null)
                                        fileCallback = null
                                    }.isSuccess
                            }
                        }
                    webViewClient =
                        object : WebViewClient() {
                            override fun onRenderProcessGone(
                                view: WebView,
                                detail: RenderProcessGoneDetail,
                            ): Boolean {
                                if (renderProcessCrashes < 2) {
                                    renderProcessCrashes++
                                } else {
                                    bridge.unavailableReason = "Die Webseite wurde wiederholt unerwartet beendet. Öffne beste.schule stattdessen im Browser."
                                }
                                return true
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView,
                                request: WebResourceRequest,
                            ): Boolean {
                                val target = request.url.toString()
                                if (!request.isForMainFrame || BesteSchuleWebsite.isInternalUrl(target)) return false
                                bridge.openExternal(target)
                                return true
                            }

                            override fun onPageStarted(
                                view: WebView,
                                url: String?,
                                favicon: Bitmap?,
                            ) {
                                if (!hasDocumentStartScript && BesteSchuleWebsite.isInternalUrl(url)) view.evaluateJavascript(script, null)
                            }

                            override fun doUpdateVisitedHistory(
                                view: WebView,
                                url: String?,
                                isReload: Boolean,
                            ) {
                                canGoBack = view.canGoBack()
                            }
                        }
                    addOnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
                        ViewCompat.getRootWindowInsets(view)?.let {
                            bridge.systemBottomInset = it.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom / view.resources.displayMetrics.density
                        }
                    }
                    webView = this
                    val target = this
                    bridge.attach { js ->
                        runCatching { if (BesteSchuleWebsite.isInternalUrl(target.url)) target.evaluateJavascript(js, null) }
                    }
                    loadUrl(url)
                }
            },
            modifier = modifier,
            onRelease = {
                bridge.attach(null)
                webView = null
                fileCallback?.onReceiveValue(null)
                fileCallback = null
                runCatching {
                    it.stopLoading()
                    if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER)) {
                        WebViewCompat.removeWebMessageListener(it, NATIVE_INTERFACE)
                    } else {
                        it.removeJavascriptInterface(NATIVE_INTERFACE)
                    }
                    it.clearCache(true)
                }
                it.destroy()
                WebStorage.getInstance().deleteAllData()
                CookieManager.getInstance().removeAllCookies(null)
            },
        )
    }
}

private fun handleMessage(
    webView: WebView,
    bridge: BesteSchuleWebBridge,
    data: String?,
) {
    val message = runCatching { Json.parseToJsonElement(data ?: return).jsonObject }.getOrNull() ?: return
    val type = message["type"]?.jsonPrimitive?.contentOrNull ?: return
    val id = message["id"]?.jsonPrimitive?.contentOrNull ?: return
    bridge.handle(type, id, message["arg"]?.jsonPrimitive?.contentOrNull.orEmpty()) { script ->
        runCatching {
            if (BesteSchuleWebsite.isInternalUrl(webView.url)) webView.evaluateJavascript(script, null)
        }
    }
}

private class NativeInterface(
    private val webView: WebView,
    private val bridge: BesteSchuleWebBridge,
) {
    private val mainHandler = Handler(Looper.getMainLooper())

    @Suppress("unused")
    @JavascriptInterface
    fun post(message: String?) {
        mainHandler.post { handleMessage(webView, bridge, message) }
    }
}

internal actual fun encodeJpeg(image: ImageBitmap): ByteArray? = null
