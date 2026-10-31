package com.hansholz.bestenotenapp.updates

import bestenotenapp.composeApp.BuildConfig
import com.hansholz.bestenotenapp.api.createHttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import platform.UIKit.UIDevice

private const val APP_STORE_ID = "6816709897"

private val lookupJson = Json { ignoreUnknownKeys = true }

private var storeUpdatePrompt: ((String) -> Unit)? = null

fun setStoreUpdatePrompt(prompt: (String) -> Unit) {
    storeUpdatePrompt = prompt
}

@Serializable
private data class AppStoreLookup(
    val results: List<AppStoreLookupResult> = emptyList(),
)

@Serializable
private data class AppStoreLookupResult(
    val version: String,
    val minimumOsVersion: String = "0",
)

internal actual object StoreUpdate {
    private var availableVersion: String? = null

    actual suspend fun checkForUpdate(onDownloaded: () -> Unit): StoreUpdateStatus {
        val client = createHttpClient()
        return try {
            val response = client.get("https://itunes.apple.com/lookup?id=$APP_STORE_ID&country=de")
            if (!response.status.isSuccess()) return StoreUpdateStatus.None
            val result =
                lookupJson
                    .decodeFromString<AppStoreLookup>(response.bodyAsText())
                    .results
                    .firstOrNull() ?: return StoreUpdateStatus.None
            val installable = !isNewerVersion(result.minimumOsVersion, UIDevice.currentDevice.systemVersion)
            if (!installable || !isNewerVersion(result.version, BuildConfig.VERSION_NAME)) return StoreUpdateStatus.None
            availableVersion = result.version
            StoreUpdateStatus.Available
        } finally {
            client.close()
        }
    }

    actual fun startUpdate() {
        availableVersion?.let { storeUpdatePrompt?.invoke(it) }
    }

    actual fun completeUpdate() = Unit
}
