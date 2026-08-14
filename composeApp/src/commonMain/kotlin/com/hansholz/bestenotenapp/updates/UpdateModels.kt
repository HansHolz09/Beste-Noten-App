package com.hansholz.bestenotenapp.updates

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GitHubRelease(
    @SerialName("tag_name") val tagName: String,
    val body: String = "",
    val assets: List<GitHubReleaseAsset> = emptyList(),
)

@Serializable
internal data class GitHubReleaseAsset(
    val name: String,
    val size: Long,
    @SerialName("browser_download_url") val browserDownloadUrl: String,
)

internal data class AvailableUpdate(
    val version: String,
    val size: String,
    val releaseNotes: String,
    val downloadUrl: String,
)

internal data class UpdateAssetTarget(
    val platform: String,
    val architecture: String? = null,
    val extension: String,
)

internal expect fun updateAssetTarget(): UpdateAssetTarget?
