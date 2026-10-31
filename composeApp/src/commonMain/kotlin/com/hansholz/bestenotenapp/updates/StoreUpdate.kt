package com.hansholz.bestenotenapp.updates

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.dokar.sonner.TextToastAction
import com.dokar.sonner.Toast
import com.dokar.sonner.ToastType
import com.dokar.sonner.ToasterState
import com.hansholz.bestenotenapp.security.kSafeProviderCompose
import kotlinx.coroutines.CancellationException
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

internal enum class StoreUpdateStatus { None, Available, Downloaded }

internal expect object StoreUpdate {
    suspend fun checkForUpdate(onDownloaded: () -> Unit): StoreUpdateStatus

    fun startUpdate()

    fun completeUpdate()
}

@Composable
internal fun StoreUpdateHost(toaster: ToasterState) =
    kSafeProviderCompose {
        LaunchedEffect(Unit) {
            val showDownloadedToast = {
                toaster.show(
                    Toast(
                        message = "Update bereit",
                        action = TextToastAction("Neustarten") { StoreUpdate.completeUpdate() },
                        type = ToastType.Success,
                        duration = Duration.INFINITE,
                    ),
                )
            }
            try {
                when (StoreUpdate.checkForUpdate(showDownloadedToast)) {
                    StoreUpdateStatus.Downloaded -> {
                        showDownloadedToast()
                    }

                    StoreUpdateStatus.Available -> {
                        val now = Clock.System.now().toEpochMilliseconds()
                        if (now < get("storeUpdateLastPromptAt", 0L) + 6.hours.inWholeMilliseconds) return@LaunchedEffect
                        put("storeUpdateLastPromptAt", now)
                        StoreUpdate.startUpdate()
                    }

                    StoreUpdateStatus.None -> {}
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
