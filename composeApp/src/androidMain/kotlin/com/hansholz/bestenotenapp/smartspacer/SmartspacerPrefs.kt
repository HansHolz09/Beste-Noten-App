package com.hansholz.bestenotenapp.smartspacer

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.edit
import com.hansholz.bestenotenapp.api.models.JournalDay
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.ZonedDateTime

object SmartspacerPrefs {
    private fun prefs(ctx: Context) = ctx.getSharedPreferences("smartspacer_prefs", Context.MODE_PRIVATE)

    fun setTokenState(
        ctx: Context,
        stored: Boolean,
    ) {
        prefs(ctx).edit { putBoolean("tokenState", stored) }
    }

    fun getTokenState(ctx: Context): Boolean = prefs(ctx).getBoolean("tokenState", false)

    fun setDay(
        ctx: Context,
        week: JournalDay,
    ) {
        val json = Json.encodeToString(JournalDay.serializer(), week)
        prefs(ctx).edit { putString("day", json) }
    }

    fun getDay(ctx: Context): JournalDay? =
        prefs(ctx).getString("day", null)?.let {
            runCatching { Json.decodeFromString(JournalDay.serializer(), it) }.getOrNull()
        }

    fun setLastFetchNow(
        ctx: Context,
        nowMillis: Long,
    ) {
        prefs(ctx).edit { putLong("last_fetch", nowMillis) }
    }

    fun getLastFetchEpochMillis(ctx: Context): Long = prefs(ctx).getLong("last_fetch", 0L)

    @SuppressLint("NewApi")
    fun shouldFetch(
        now: ZonedDateTime,
        lastFetchMillis: Long,
    ): Boolean {
        val hour = now.hour
        if (hour !in 6..15) return false
        if (lastFetchMillis == 0L) return true
        val last = Instant.ofEpochMilli(lastFetchMillis).atZone(now.zone)
        return java.time.Duration
            .between(last, now)
            .toMinutes() >= 60
    }
}
