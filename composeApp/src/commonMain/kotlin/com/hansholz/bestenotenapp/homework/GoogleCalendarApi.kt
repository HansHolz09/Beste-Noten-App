package com.hansholz.bestenotenapp.homework

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class GoogleCalendarApi(
    private val httpClient: HttpClient,
    private val authProvider: GoogleAuthProvider,
) {
    private val baseUrl = "https://www.googleapis.com/calendar/v3"

    suspend fun createHomeworkCalendar(): GoogleCalendar =
        createCalendar(
            summary = "BNA Hausaufgaben",
            description = "Von der Beste-Noten-App für die Hausaufgabensynchronisierung erstellt",
        )

    suspend fun createCalendar(
        summary: String,
        description: String? = null,
    ): GoogleCalendar =
        httpClient
            .post("$baseUrl/calendars") {
                authorize()
                contentType(ContentType.Application.Json)
                setBody(GoogleCalendarCreateRequest(summary, description))
            }.bodyOrThrow()

    suspend fun listCalendars(pageToken: String? = null): GoogleCalendarListResponse =
        httpClient
            .get("$baseUrl/users/me/calendarList") {
                authorize()
                parameter("maxResults", 250)
                parameter("showHidden", true)
                pageToken?.let { parameter("pageToken", it) }
            }.bodyOrThrow()

    suspend fun createEvent(
        calendarId: String,
        event: GoogleCalendarEvent,
    ): GoogleCalendarEvent =
        httpClient
            .post("$baseUrl/calendars/${calendarId.encodeURLPathPart()}/events") {
                authorize()
                contentType(ContentType.Application.Json)
                setBody(event)
            }.bodyOrThrow()

    suspend fun patchEvent(
        calendarId: String,
        eventId: String,
        event: GoogleCalendarEvent,
    ): GoogleCalendarEvent =
        httpClient
            .patch("$baseUrl/calendars/${calendarId.encodeURLPathPart()}/events/${eventId.encodeURLPathPart()}") {
                authorize()
                contentType(ContentType.Application.Json)
                setBody(event)
            }.bodyOrThrow()

    suspend fun deleteEvent(
        calendarId: String,
        eventId: String,
    ) {
        val response =
            httpClient.delete("$baseUrl/calendars/${calendarId.encodeURLPathPart()}/events/${eventId.encodeURLPathPart()}") {
                authorize()
            }
        if (!response.status.isSuccess() && response.status != HttpStatusCode.NotFound) {
            response.throwGoogleError()
        }
    }

    suspend fun listEvents(
        calendarId: String,
        syncToken: String?,
        pageToken: String? = null,
        privateExtendedProperty: String? = null,
    ): GoogleCalendarEventsResponse {
        val response =
            httpClient.get("$baseUrl/calendars/${calendarId.encodeURLPathPart()}/events") {
                authorize()
                parameter("singleEvents", true)
                parameter("showDeleted", true)
                parameter("maxResults", 2500)
                syncToken?.let { parameter("syncToken", it) }
                pageToken?.let { parameter("pageToken", it) }
                privateExtendedProperty?.let { parameter("privateExtendedProperty", it) }
            }
        if (response.status == HttpStatusCode.Gone) {
            throw InvalidGoogleCalendarSyncTokenException()
        }
        return response.bodyOrThrow()
    }

    private suspend fun io.ktor.client.request.HttpRequestBuilder.authorize() {
        val token = authProvider.getAccessToken() ?: throw MissingGoogleAuthException()
        bearerAuth(token)
        header(HttpHeaders.Accept, ContentType.Application.Json)
    }

    private suspend inline fun <reified T> HttpResponse.bodyOrThrow(): T {
        if (!status.isSuccess()) throwGoogleError()
        return body()
    }

    private suspend fun HttpResponse.throwGoogleError(): Nothing {
        val responseBody = bodyAsText()
        val detail =
            runCatching {
                Json
                    .parseToJsonElement(responseBody)
                    .jsonObject["error"]
                    ?.jsonObject
                    ?.get("message")
                    ?.jsonPrimitive
                    ?.content
            }.getOrNull()
        throw GoogleCalendarApiException(status.value, detail ?: responseBody.take(500))
    }
}

class InvalidGoogleCalendarSyncTokenException : IllegalStateException("Google Calendar SyncToken ist ungültig.")

class GoogleCalendarApiException(
    val status: Int,
    detail: String,
) : IllegalStateException("Google Calendar ($status): $detail")

class GoogleCalendarHomeworkSyncDataSource(
    private val api: GoogleCalendarApi,
    private val studentIdProvider: () -> String?,
) {
    suspend fun getOrCreateCalendar(preferredCalendarId: String?): GoogleCalendar {
        val calendars = mutableListOf<GoogleCalendarListEntry>()
        var pageToken: String? = null
        do {
            val response = api.listCalendars(pageToken)
            calendars += response.items
            pageToken = response.nextPageToken
        } while (pageToken != null)

        val candidates =
            calendars.filter {
                !it.deleted &&
                    it.accessRole in listOf("owner", "writer") &&
                    (
                        it.summary == "BNA Hausaufgaben" ||
                            it.description == "Von der Beste-Noten-App für die Hausaufgabensynchronisierung erstellt"
                    )
            }
        val candidatesWithEventCount = mutableListOf<Pair<GoogleCalendarListEntry, Int>>()
        candidates.forEach { calendar ->
            try {
                var eventPageToken: String? = null
                var eventCount = 0
                do {
                    val response =
                        api.listEvents(
                            calendarId = calendar.id,
                            syncToken = null,
                            pageToken = eventPageToken,
                            privateExtendedProperty = "app=beste-noten-app",
                        )
                    eventCount += response.items.size
                    eventPageToken = response.nextPageToken
                } while (eventPageToken != null)
                candidatesWithEventCount += calendar to eventCount
            } catch (e: GoogleCalendarApiException) {
                if (e.status != HttpStatusCode.Forbidden.value) throw e
            }
        }

        val highestEventCount = candidatesWithEventCount.maxOfOrNull { it.second }
        val calendar =
            candidatesWithEventCount
                .firstOrNull { it.second == highestEventCount && it.first.id == preferredCalendarId }
                ?.first
                ?: candidatesWithEventCount
                    .firstOrNull { it.second == highestEventCount }
                    ?.first
                ?: return api.createHomeworkCalendar()
        return GoogleCalendar(calendar.id, calendar.summary, calendar.description)
    }

    suspend fun pushEntry(
        calendarId: String,
        entry: HomeworkEntry,
        mapping: CalendarSyncMapping?,
    ): CalendarSyncMapping {
        val event = entry.toGoogleCalendarEvent(studentIdProvider())
        val saved =
            if (mapping?.googleEventId == null) {
                api.createEvent(calendarId, event)
            } else {
                api.patchEvent(calendarId, mapping.googleEventId, event)
            }
        return CalendarSyncMapping(
            localId = entry.localId,
            googleCalendarId = calendarId,
            googleEventId = saved.id,
            googleEtag = saved.etag,
            googleUpdatedAt = remoteUpdatedAt(saved),
            syncState = SyncState.SYNCED,
            lastSyncedHash = entry.stableSyncHash(),
        )
    }

    suspend fun deleteEntry(
        calendarId: String,
        mapping: CalendarSyncMapping?,
    ) {
        mapping?.googleEventId?.let { api.deleteEvent(calendarId, it) }
    }

    suspend fun loadEvents(
        calendarId: String,
        syncToken: String?,
    ): RemoteCalendarEvents {
        val events = mutableListOf<GoogleCalendarEvent>()
        var pageToken: String? = null
        var nextSyncToken: String? = null
        do {
            val response = api.listEvents(calendarId, syncToken, pageToken)
            events += response.items
            pageToken = response.nextPageToken
            nextSyncToken = response.nextSyncToken ?: nextSyncToken
        } while (pageToken != null)
        return RemoteCalendarEvents(events, nextSyncToken)
    }
}
