package com.hansholz.bestenotenapp.api

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import io.ktor.client.HttpClient
import io.ktor.client.plugins.cookies.cookies
import io.ktor.client.request.delete
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import io.ktor.http.renderCookieHeader
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.random.Random
import kotlin.time.Clock

sealed interface PasswordLoginStep {
    data object TwoFactorRequired : PasswordLoginStep

    data class Complete(
        val credential: ManagedPersonalAccessToken,
    ) : PasswordLoginStep
}

data class ManagedPersonalAccessToken(
    val token: String,
    val id: String,
    val sessionCookies: String,
)

class BesteSchulePasswordLogin {
    private var client: HttpClient = createHttpClient(forLogin = true)
    private var challengeToken: String? = null
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun submitCredentials(
        identifier: String,
        password: String,
    ): PasswordLoginStep {
        client.close()
        client = createHttpClient(forLogin = true)
        challengeToken = null

        val page = client.get("$BASE/login") { header(HttpHeaders.Accept, "text/html") }
        val document = html(page)
        val form =
            document.select("form").firstOrNull { it.select("input[name=identifier]").isNotEmpty() && it.select("input[name=password]").isNotEmpty() }
                ?: throw PasswordLoginException("Das Anmeldeformular von beste.schule konnte nicht gelesen werden.")
        val csrf =
            form.selectFirst("input[name=_token]")?.attr("value")?.takeIf { it.isNotBlank() }
                ?: throw PasswordLoginException("Der Anmeldeschutz von beste.schule fehlt.")
        requireForm(form, "/login")

        val response =
            client.submitForm(
                url = "$BASE/login",
                formParameters =
                    Parameters.build {
                        append("_token", csrf)
                        append("identifier", identifier)
                        append("password", password)
                    },
            ) {
                header(HttpHeaders.Accept, "text/html")
                header(HttpHeaders.Origin, BASE)
            }
        return afterPost(response, "Die Zugangsdaten wurden nicht akzeptiert.")
    }

    suspend fun submitCode(code: String): PasswordLoginStep {
        val csrf = challengeToken ?: throw PasswordLoginException("Bitte beginne die Anmeldung erneut.")
        if (code.length != 6 || !code.all(Char::isDigit)) throw PasswordLoginException("Bitte gib den sechsstelligen Code ein.")
        val response =
            client.submitForm(
                url = "$BASE/2fa",
                formParameters =
                    Parameters.build {
                        append("_token", csrf)
                        append("one_time_password", code)
                    },
            ) {
                header(HttpHeaders.Accept, "text/html")
                header(HttpHeaders.Origin, BASE)
            }
        return afterPost(response, "Der Code wurde nicht akzeptiert.")
    }

    fun close() = client.close()

    private suspend fun afterPost(
        response: HttpResponse,
        rejectedMessage: String,
    ): PasswordLoginStep {
        if (response.status == HttpStatusCode.Found) {
            requireRedirectToHome(response)
        } else if (response.status != HttpStatusCode.OK) {
            throw PasswordLoginException(rejectedMessage)
        }
        val home = client.get("$BASE/home") { header(HttpHeaders.Accept, "text/html") }
        val document = html(home)
        val challenge =
            document.select("form").firstOrNull {
                it.select("input[name=one_time_password]").isNotEmpty() && isForm(it, "/2fa")
            }
        if (challenge != null) {
            challengeToken = challenge.selectFirst("input[name=_token]")?.attr("value")?.takeIf { it.isNotBlank() }
                ?: throw PasswordLoginException("Der 2FA-Anmeldeschutz fehlt.")
            return PasswordLoginStep.TwoFactorRequired
        }
        if (document.select("input[name=password]").isNotEmpty()) throw PasswordLoginException(rejectedMessage)
        val hasAppModule =
            document.select("script[type=module][src]").any {
                val source = it.absUrl("src")
                sameOrigin(source) && Url(source).encodedPath.matches(Regex("/build/assets/app-[A-Za-z0-9_-]+\\.js"))
            }
        if (home.status != HttpStatusCode.OK || document.selectFirst("#app") == null || !hasAppModule) {
            throw PasswordLoginException("Die Anmeldung konnte nicht eindeutig bestätigt werden.")
        }
        challengeToken = null
        return PasswordLoginStep.Complete(createPersonalAccessToken())
    }

    private suspend fun createPersonalAccessToken(): ManagedPersonalAccessToken {
        val before = tokenList(client).map { it.id }.toSet()
        val csrf = fetchCsrfToken(client)
        val name = "Beste-Noten-App-${Clock.System.now().epochSeconds}-${Random.nextInt().toUInt().toString(16)}"
        val response =
            client.post("$BASE/oauth/personal-access-tokens") {
                header(HttpHeaders.Accept, "application/json")
                header(HttpHeaders.Origin, BASE)
                header("Referer", "$BASE/me/passport")
                header("X-CSRF-TOKEN", csrf)
                contentType(ContentType.Application.Json)
                setBody("""{"name":${Json.encodeToString(name)},"scopes":["api:read","api:write"]}""")
            }
        if (response.status != HttpStatusCode.OK && response.status != HttpStatusCode.Created) {
            throw PasswordLoginException("Der Zugriffstoken konnte nicht erstellt werden.")
        }
        val token =
            runCatching {
                json
                    .parseToJsonElement(response.bodyAsText())
                    .jsonObject["accessToken"]
                    ?.jsonPrimitive
                    ?.content
            }.getOrNull()
                ?.takeIf { it.isNotBlank() }
                ?: throw PasswordLoginException("Die Token-Antwort von beste.schule war unvollständig.")
        val after = tokenList(client).filter { it.id !in before && it.name == name }
        if (after.size != 1) {
            throw PasswordLoginException("Der neue Token konnte nicht eindeutig zugeordnet werden. Bitte prüfe ‚$name‘ in der Tokenverwaltung.")
        }
        val id = after.single().id
        try {
            val apiClient = createHttpClient()
            try {
                val check =
                    apiClient.get("$BASE/api/user") {
                        header(HttpHeaders.Accept, "application/json")
                        header(HttpHeaders.Authorization, "Bearer $token")
                    }
                val valid =
                    check.status == HttpStatusCode.OK &&
                        runCatching { json.parseToJsonElement(check.bodyAsText()).jsonObject["data"] is JsonObject }.getOrDefault(false)
                if (!valid) throw PasswordLoginException("Der neue Token wurde von der API nicht akzeptiert.")
            } finally {
                apiClient.close()
            }
        } catch (error: Exception) {
            runCatching { revoke(id, sessionCookies()) }
            throw error
        }
        return ManagedPersonalAccessToken(token, id, sessionCookies())
    }

    private suspend fun sessionCookies(): String = client.cookies("$BASE/me/passport").joinToString("; ") { renderCookieHeader(it) }

    private suspend fun tokenList(client: HttpClient): List<TokenRow> {
        val response = client.get("$BASE/oauth/personal-access-tokens") { header(HttpHeaders.Accept, "application/json") }
        if (response.status != HttpStatusCode.OK) throw PasswordLoginException("Die Tokenliste konnte nicht gelesen werden.")
        val items =
            runCatching { json.parseToJsonElement(response.bodyAsText()) as JsonArray }.getOrNull()
                ?: throw PasswordLoginException("Die Tokenliste hatte ein unerwartetes Format.")
        return items.map {
            val row = it as? JsonObject ?: throw PasswordLoginException("Die Tokenliste hatte ein unerwartetes Format.")
            TokenRow(row["id"]?.jsonPrimitive?.content.orEmpty(), row["name"]?.jsonPrimitive?.content.orEmpty())
                .also { token -> if (token.id.isBlank()) throw PasswordLoginException("Die Tokenliste hatte ein unerwartetes Format.") }
        }
    }

    private suspend fun html(response: HttpResponse): Document {
        if (response.status != HttpStatusCode.OK || response.headers[HttpHeaders.ContentLength]?.toLongOrNull()?.let { it > MAX_HTML_BYTES } == true) {
            throw PasswordLoginException("beste.schule hat eine unerwartete Anmeldeseite geliefert.")
        }
        val body = response.bodyAsText()
        if (body.length > MAX_HTML_BYTES) throw PasswordLoginException("Die Anmeldeseite ist unerwartet groß.")
        return Ksoup.parse(
            body,
            response.call.request.url
                .toString(),
        )
    }

    @Suppress("SameParameterValue")
    private fun requireForm(
        form: Element,
        path: String,
    ) {
        if (!isForm(form, path)) throw PasswordLoginException("Das Anmeldeformular hat sich geändert.")
    }

    private fun isForm(
        form: Element,
        path: String,
    ): Boolean {
        val action = form.absUrl("action")
        return form.attr("method").equals("post", ignoreCase = true) && sameOrigin(action) && Url(action).encodedPath == path
    }

    private fun requireRedirectToHome(response: HttpResponse) {
        val location = response.headers[HttpHeaders.Location].orEmpty()
        val url = if (location.startsWith("/")) "$BASE$location" else location
        if (!sameOrigin(url) || Url(url).encodedPath != "/home") {
            throw PasswordLoginException("beste.schule hat die Anmeldung anders weitergeleitet.")
        }
    }

    private fun sameOrigin(value: String): Boolean =
        runCatching { Url(value).let { it.protocol.name == "https" && it.host == "beste.schule" && it.port == 443 } }.getOrDefault(false)

    private data class TokenRow(
        val id: String,
        val name: String,
    )

    companion object {
        private const val BASE = "https://beste.schule"
        private const val MAX_HTML_BYTES = 4_000_000

        private suspend fun fetchCsrfToken(
            client: HttpClient,
            cookieHeader: String? = null,
        ): String {
            val response =
                client.get("$BASE/me/passport") {
                    header(HttpHeaders.Accept, "text/html")
                    if (cookieHeader != null) header(HttpHeaders.Cookie, cookieHeader)
                }
            if (response.status != HttpStatusCode.OK) throw PasswordLoginException("Die Tokenverwaltung ist nicht mehr angemeldet.")
            return Ksoup
                .parse(response.bodyAsText())
                .selectFirst("meta[name=csrf-token]")
                ?.attr("content")
                ?.takeIf { it.isNotBlank() }
                ?: throw PasswordLoginException("Der Anmeldeschutz der Tokenverwaltung fehlt.")
        }

        suspend fun revoke(
            id: String,
            cookieHeader: String,
        ): Boolean {
            if (id.isBlank() || cookieHeader.isBlank()) return false
            val client = createHttpClient(forLogin = true)
            try {
                val csrf = fetchCsrfToken(client, cookieHeader)
                val response =
                    client.delete("$BASE/oauth/personal-access-tokens/${id.encodeURLPathPart()}") {
                        header(HttpHeaders.Accept, "application/json")
                        header(HttpHeaders.Origin, BASE)
                        header("Referer", "$BASE/me/passport")
                        header("X-CSRF-TOKEN", csrf)
                    }
                return response.status == HttpStatusCode.NoContent || response.status == HttpStatusCode.OK
            } finally {
                client.close()
            }
        }
    }
}

class PasswordLoginException(
    message: String,
) : Exception(message)
