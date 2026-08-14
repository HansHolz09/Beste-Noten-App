package com.hansholz.bestenotenapp.updates

import bestenotenapp.composeApp.BuildConfig
import com.hansholz.bestenotenapp.api.createHttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header

private const val LATEST_RELEASE_URL = "https://api.github.com/repos/HansHolz09/Beste-Noten-App/releases/latest"

internal suspend fun checkForUpdate(): AvailableUpdate? {
    val client = createHttpClient()

    return try {
        val release =
            client
                .get(LATEST_RELEASE_URL) {
                    header("Accept", "application/vnd.github+json")
                    header("X-GitHub-Api-Version", "2022-11-28")
                    header("User-Agent", "Beste-Noten-App/${BuildConfig.VERSION_NAME}")
                }.body<GitHubRelease>()
        val releaseVersion = release.tagName.removePrefix("v")
        if (!isNewerVersion(releaseVersion, BuildConfig.VERSION_NAME)) return null

        val target = updateAssetTarget() ?: return null
        val asset = release.assets.firstOrNull { it.matches(target) } ?: return null
        AvailableUpdate(
            version = releaseVersion,
            releaseNotes = release.body,
            downloadUrl = asset.browserDownloadUrl,
        )
    } finally {
        client.close()
    }
}

private fun GitHubReleaseAsset.matches(target: UpdateAssetTarget): Boolean {
    val normalizedName = name.lowercase()
    val platformMarker = "-${target.platform.lowercase()}-"
    val architectureMatches = target.architecture?.let { normalizedName.contains("-$it") } ?: true
    return normalizedName.contains(platformMarker) &&
        architectureMatches &&
        normalizedName.endsWith(target.extension.lowercase())
}

internal fun isNewerVersion(
    candidate: String,
    current: String,
): Boolean {
    fun parts(version: String) =
        version
            .removePrefix("v")
            .substringBefore('-')
            .split('.')
            .map { part -> part.takeWhile(Char::isDigit).toIntOrNull() ?: 0 }

    val candidateParts = parts(candidate)
    val currentParts = parts(current)
    repeat(maxOf(candidateParts.size, currentParts.size)) { index ->
        val candidatePart = candidateParts.getOrElse(index) { 0 }
        val currentPart = currentParts.getOrElse(index) { 0 }
        if (candidatePart != currentPart) return candidatePart > currentPart
    }
    return false
}
