package com.hansholz.bestenotenapp.api

import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.bearerAuth

class BesteSchulePluginConfig {
    var accessTokenProvider: suspend () -> String? = { null }
    var studentIdProvider: () -> String? = { null }
}

val BesteSchulePlugin =
    createClientPlugin("BesteSchulePlugin", ::BesteSchulePluginConfig) {
        val accessTokenProvider = pluginConfig.accessTokenProvider
        val studentIdProvider = pluginConfig.studentIdProvider

        onRequest { request, _ ->
            accessTokenProvider()?.let { request.bearerAuth(it) }
        }

        on(Send) { request ->
            val studentId = studentIdProvider()
            if (!studentId.isNullOrBlank() && request.url.pathSegments.none(STUDENT_FILTER_EXCLUDED_PATHS::contains)) {
                if (!request.url.parameters.contains("filter[student]")) {
                    request.url.parameters.append("filter[student]", studentId)
                }
            }
            proceed(request)
        }
    }

private val STUDENT_FILTER_EXCLUDED_PATHS = setOf("subjects")
