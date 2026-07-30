package com.hansholz.bestenotenapp.homework

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleCalendarCreateRequest(
    val summary: String,
    val description: String? = null,
)

@Serializable
data class GoogleCalendar(
    val id: String,
    val summary: String? = null,
    val description: String? = null,
)

@Serializable
data class GoogleCalendarListResponse(
    val items: List<GoogleCalendarListEntry> = emptyList(),
    val nextPageToken: String? = null,
)

@Serializable
data class GoogleCalendarListEntry(
    val id: String,
    val summary: String? = null,
    val description: String? = null,
    val accessRole: String? = null,
    val deleted: Boolean = false,
)

@Serializable
data class GoogleCalendarEventsResponse(
    val items: List<GoogleCalendarEvent> = emptyList(),
    val nextSyncToken: String? = null,
    val nextPageToken: String? = null,
)

@Serializable
data class GoogleCalendarEvent(
    val id: String? = null,
    val etag: String? = null,
    val summary: String? = null,
    val description: String? = null,
    val start: GoogleCalendarEventDateTime? = null,
    val end: GoogleCalendarEventDateTime? = null,
    val updated: String? = null,
    val status: String? = null,
    val transparency: String? = null,
    val visibility: String? = null,
    val reminders: GoogleCalendarReminders? = null,
    val extendedProperties: GoogleCalendarExtendedProperties? = null,
)

@Serializable
data class GoogleCalendarEventDateTime(
    val date: String? = null,
    val dateTime: String? = null,
    val timeZone: String? = null,
)

@Serializable
data class GoogleCalendarReminders(
    val useDefault: Boolean = true,
    val overrides: List<GoogleCalendarReminderOverride> = emptyList(),
)

@Serializable
data class GoogleCalendarReminderOverride(
    val method: String,
    val minutes: Int,
)

@Serializable
data class GoogleCalendarExtendedProperties(
    @SerialName("private") val privateProperties: Map<String, String> = emptyMap(),
)

data class RemoteCalendarEvents(
    val events: List<GoogleCalendarEvent>,
    val nextSyncToken: String?,
)
