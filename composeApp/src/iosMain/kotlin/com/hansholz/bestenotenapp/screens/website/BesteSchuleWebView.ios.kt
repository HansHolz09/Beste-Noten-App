package com.hansholz.bestenotenapp.screens.website

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import com.hansholz.bestenotenapp.main.LocalNativeComponentsEnabled
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.readValue
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import platform.CoreGraphics.CGBitmapContextCreate
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGColorSpaceRelease
import platform.CoreGraphics.CGContextDrawImage
import platform.CoreGraphics.CGContextRelease
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGRectZero
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.UIKit.UIColor
import platform.UIKit.UIImage
import platform.UIKit.UIScrollViewContentInsetAdjustmentBehavior
import platform.WebKit.WKNavigationAction
import platform.WebKit.WKNavigationActionPolicy
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKScriptMessage
import platform.WebKit.WKScriptMessageHandlerProtocol
import platform.WebKit.WKSnapshotConfiguration
import platform.WebKit.WKUserContentController
import platform.WebKit.WKUserScript
import platform.WebKit.WKUserScriptInjectionTime
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.WebKit.WKWebsiteDataStore
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.WeakReference

internal actual val isBesteSchuleWebViewSupported = true

@Composable
internal actual fun isBesteSchuleWebViewTransparent(): Boolean = LocalNativeComponentsEnabled.current.value

private const val MESSAGE_HANDLER = "BesteNotenApp"

@OptIn(ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
@Composable
internal actual fun BesteSchuleWebView(
    url: String,
    bridge: BesteSchuleWebBridge,
    transparent: Boolean,
    backgroundColor: Color,
    modifier: Modifier,
) {
    val messageHandler = remember(bridge) { MessageHandler(bridge) }
    val navigationDelegate = remember(bridge) { NavigationDelegate(bridge) }

    UIKitView(
        factory = {
            val configuration =
                WKWebViewConfiguration().apply {
                    websiteDataStore = WKWebsiteDataStore.nonPersistentDataStore()
                    applicationNameForUserAgent = "Mobile/15E148 ${BesteSchuleWebsite.USER_AGENT_TOKEN}"
                    userContentController.addUserScript(
                        WKUserScript(
                            source =
                                bridge.script(
                                    "ios",
                                    "window.webkit.messageHandlers.$MESSAGE_HANDLER.postMessage({ type: type, id: id, arg: arg });",
                                ),
                            injectionTime = WKUserScriptInjectionTime.WKUserScriptInjectionTimeAtDocumentStart,
                            forMainFrameOnly = true,
                        ),
                    )
                    userContentController.addScriptMessageHandler(messageHandler, MESSAGE_HANDLER)
                }
            WKWebView(frame = CGRectZero.readValue(), configuration = configuration).apply {
                this.navigationDelegate = navigationDelegate
                allowsBackForwardNavigationGestures = true
                scrollView.showsVerticalScrollIndicator = false
                scrollView.showsHorizontalScrollIndicator = false
                scrollView.contentInsetAdjustmentBehavior =
                    UIScrollViewContentInsetAdjustmentBehavior.UIScrollViewContentInsetAdjustmentNever
                val fill =
                    if (transparent) {
                        UIColor.clearColor
                    } else {
                        UIColor(
                            red = backgroundColor.red.toDouble(),
                            green = backgroundColor.green.toDouble(),
                            blue = backgroundColor.blue.toDouble(),
                            alpha = 1.0,
                        )
                    }
                opaque = !transparent
                this.backgroundColor = fill
                scrollView.backgroundColor = fill
                messageHandler.attach(this)
                val target = this
                bridge.snapshot = { target.snapshot() }
                bridge.attach { js ->
                    if (BesteSchuleWebsite.isInternalUrl(target.URL?.absoluteString)) target.evaluateJavaScript(js, null)
                }
                NSURL.URLWithString(url)?.let { loadRequest(NSURLRequest.requestWithURL(it)) }
            }
        },
        modifier = modifier,
        onRelease = {
            bridge.attach(null)
            bridge.snapshot = null
            it.stopLoading()
            it.navigationDelegate = null
            it.configuration.userContentController.removeScriptMessageHandlerForName(MESSAGE_HANDLER)
            it.configuration.userContentController.removeAllUserScripts()
        },
        properties =
            UIKitInteropProperties(
                interactionMode = UIKitInteropInteractionMode.NonCooperative,
                isNativeAccessibilityEnabled = true,
                placedAsOverlay = transparent,
            ),
    )
}

@OptIn(ExperimentalNativeApi::class)
private class MessageHandler(
    private val bridge: BesteSchuleWebBridge,
) : NSObject(),
    WKScriptMessageHandlerProtocol {
    private var webView: WeakReference<WKWebView>? = null

    fun attach(webView: WKWebView) {
        this.webView = WeakReference(webView)
    }

    override fun userContentController(
        userContentController: WKUserContentController,
        didReceiveScriptMessage: WKScriptMessage,
    ) {
        val frame = didReceiveScriptMessage.frameInfo
        if (!frame.mainFrame || frame.securityOrigin.protocol != "https" || frame.securityOrigin.host != "beste.schule") return
        val body = didReceiveScriptMessage.body as? Map<*, *> ?: return
        val type = body["type"] as? String ?: return
        val id = body["id"] as? String ?: return
        bridge.handle(type, id, body["arg"] as? String ?: "") { script ->
            val target = webView?.get() ?: return@handle
            if (BesteSchuleWebsite.isInternalUrl(target.URL?.absoluteString)) target.evaluateJavaScript(script, null)
        }
    }
}

private class NavigationDelegate(
    private val bridge: BesteSchuleWebBridge,
) : NSObject(),
    WKNavigationDelegateProtocol {
    override fun webView(
        webView: WKWebView,
        decidePolicyForNavigationAction: WKNavigationAction,
        decisionHandler: (WKNavigationActionPolicy) -> Unit,
    ) {
        val target = decidePolicyForNavigationAction.request.URL?.absoluteString
        val targetFrame = decidePolicyForNavigationAction.targetFrame
        when {
            targetFrame != null && !targetFrame.mainFrame -> {
                decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
            }

            BesteSchuleWebsite.isInternalUrl(target) && targetFrame != null -> {
                decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
            }

            BesteSchuleWebsite.isInternalUrl(target) -> {
                decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyCancel)
                webView.loadRequest(decidePolicyForNavigationAction.request)
            }

            else -> {
                decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyCancel)
                target?.let(bridge::openExternal)
            }
        }
    }
}

private suspend fun WKWebView.snapshot(): ImageBitmap? =
    suspendCancellableCoroutine { continuation ->
        val configuration = WKSnapshotConfiguration().apply { afterScreenUpdates = false }
        takeSnapshotWithConfiguration(configuration) { image, _ -> continuation.resume(image?.toImageBitmap()) }
    }

@OptIn(ExperimentalForeignApi::class)
private fun UIImage.toImageBitmap(): ImageBitmap? {
    val image = CGImage ?: return null
    val width = CGImageGetWidth(image).toInt()
    val height = CGImageGetHeight(image).toInt()
    val pixels = ByteArray(width * height * 4)
    val colorSpace = CGColorSpaceCreateDeviceRGB()
    pixels.usePinned { pinned ->
        val context =
            CGBitmapContextCreate(
                pinned.addressOf(0),
                width.toULong(),
                height.toULong(),
                8u,
                (width * 4).toULong(),
                colorSpace,
                CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value,
            )
        CGContextDrawImage(context, CGRectMake(0.0, 0.0, width.toDouble(), height.toDouble()), image)
        CGContextRelease(context)
    }
    CGColorSpaceRelease(colorSpace)
    return Image.makeRaster(ImageInfo(width, height, ColorType.RGBA_8888, ColorAlphaType.PREMUL), pixels, width * 4).toComposeImageBitmap()
}

internal actual fun encodeJpeg(image: ImageBitmap) = Image.makeFromBitmap(image.asSkiaBitmap()).encodeToData(EncodedImageFormat.JPEG, 85)?.bytes
