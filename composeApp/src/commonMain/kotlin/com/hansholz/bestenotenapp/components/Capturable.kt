package com.hansholz.bestenotenapp.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@Composable
fun rememberCaptureController(): CaptureController {
    val graphicsLayer = rememberGraphicsLayer()
    return remember(graphicsLayer) { CaptureController(graphicsLayer) }
}

fun Modifier.capturable(controller: CaptureController): Modifier =
    drawWithContent {
        val revision = controller.captureRevision
        controller.graphicsLayer.record {
            this@drawWithContent.drawContent()
        }
        drawLayer(controller.graphicsLayer)
        controller.onRecorded(revision)
    }

class CaptureController internal constructor(
    internal val graphicsLayer: GraphicsLayer,
) {
    private val mutex = Mutex()
    private var recorded = CompletableDeferred<Unit>()
    internal var captureRevision by mutableIntStateOf(0)
        private set

    suspend fun capture(): ImageBitmap =
        withContext(Dispatchers.Main) {
            mutex.withLock {
                recorded = CompletableDeferred()
                captureRevision++
                recorded.await()
                graphicsLayer.toImageBitmap()
            }
        }

    internal fun onRecorded(revision: Int) {
        if (revision == captureRevision) recorded.complete(Unit)
    }
}
