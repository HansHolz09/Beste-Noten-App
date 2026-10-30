package com.hansholz.bestenotenapp.components

import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.node.requireGraphicsContext
import com.hansholz.bestenotenapp.main.Platform
import com.hansholz.bestenotenapp.main.getPlatform
import com.hansholz.bestenotenapp.main.getPlatformVersion
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun Modifier.cacheAsBitmap(vararg keys: Any?): Modifier = if (bitmapCacheSupported) this then CacheAsBitmapElement(keys.toList()) else this

private val bitmapCacheSupported = getPlatform() != Platform.ANDROID || (getPlatformVersion()?.toIntOrNull() ?: 0) >= 28

private data class CacheAsBitmapElement(
    val keys: List<Any?>,
) : ModifierNodeElement<CacheAsBitmapNode>() {
    override fun create() = CacheAsBitmapNode()

    override fun update(node: CacheAsBitmapNode) {
        node.invalidateCache()
    }
}

private class CacheAsBitmapNode :
    Modifier.Node(),
    DrawModifierNode,
    ObserverModifierNode {
    private var layer: GraphicsLayer? = null
    private var bitmap: ImageBitmap? = null
    private var dirty = true
    private var captureJob: Job? = null

    override fun onAttach() {
        layer = requireGraphicsContext().createGraphicsLayer()
    }

    override fun onDetach() {
        captureJob = null
        bitmap = null
        dirty = true
        layer?.let { requireGraphicsContext().releaseGraphicsLayer(it) }
        layer = null
    }

    override fun onObservedReadsChanged() {
        invalidateCache()
    }

    fun invalidateCache() {
        dirty = true
        invalidateDraw()
    }

    override fun ContentDrawScope.draw() {
        val layer = layer ?: return drawContent()
        observeReads { layer.record { this@draw.drawContent() } }
        val bitmap = bitmap
        if (!dirty && bitmap != null && bitmap.width == layer.size.width && bitmap.height == layer.size.height) {
            drawImage(bitmap)
            return
        }
        this@CacheAsBitmapNode.bitmap = null
        drawLayer(layer)
        captureJob?.cancel()
        captureJob =
            coroutineScope.launch {
                withFrameNanos {}
                dirty = false
                this@CacheAsBitmapNode.bitmap = layer.toImageBitmap()
                invalidateDraw()
            }
    }
}
