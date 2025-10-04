package com.hansholz.bestenotenapp.api

import androidx.compose.runtime.MutableState
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin

fun studentFilterPlugin(studentId: MutableState<String?>) =
    createClientPlugin("StudentFilterPlugin") {
        on(Send) { request ->
            val id = studentId.value
            val urlBlacklist = listOf("subjects")
            if (!id.isNullOrBlank() && !request.url.pathSegments.any { urlBlacklist.contains(it) }) {
                val url = request.url
                if (!url.parameters.contains("filter[student]")) {
                    url.parameters.append("filter[student]", id)
                }
            }
            proceed(request)
        }
    }
