package com.hansholz.bestenotenapp.smartspacer.target

import androidx.compose.runtime.mutableStateOf
import com.hansholz.bestenotenapp.api.BesteSchuleApi
import com.hansholz.bestenotenapp.api.createHttpClient
import com.hansholz.bestenotenapp.api.models.JournalDay
import com.hansholz.bestenotenapp.security.AuthTokenManager
import com.hansholz.bestenotenapp.utils.weekOfYear
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

class LessonsTargetRepository {
    private val tokenManager = AuthTokenManager()
    private val httpClient = createHttpClient()

    private val tokenState = mutableStateOf<String?>(null)
    private val api = BesteSchuleApi(httpClient, tokenState)

    fun ensureToken(): Boolean {
        tokenState.value = tokenManager.getToken()
        return !tokenState.value.isNullOrEmpty()
    }

    @OptIn(ExperimentalTime::class)
    suspend fun getCurrentJournalDay(): JournalDay? {
        if (!ensureToken()) return null

        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val nr = "${today.year}-${today.weekOfYear}"

        val week = runCatching {
            api.journalWeekShow(
                nr = nr,
                filterYear = null,
                interpolate = true,
                include = "days.lessons"
            ).data
        }.getOrNull()

        val currentDate = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
            .let {
                "${it.year}-${it.month.number.toString().padStart(2, '0')}" +
                        "-${it.day.toString().padStart(2, '0')}"
            }

        return week?.days?.find { it.date == currentDate }
    }
}
