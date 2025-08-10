package com.hansholz.bestenotenapp.smartspacer.target

import android.annotation.SuppressLint
import android.content.Context
import com.hansholz.bestenotenapp.smartspacer.SmartspacerPrefs
import com.hansholz.bestenotenapp.utils.AndroidContext
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.receivers.SmartspacerTargetUpdateReceiver
import java.time.ZonedDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LessonsTargetUpdateReceiver : SmartspacerTargetUpdateReceiver() {
    @SuppressLint("NewApi")
    override fun onRequestSmartspaceTargetUpdate(
        context: Context,
        requestTargets: List<RequestTarget>
    ) {
        AndroidContext.init(context)
        val scope = CoroutineScope(Dispatchers.IO)
        val repo = LessonsTargetRepository()
        val prefs = SmartspacerPrefs

        prefs.setTokenState(context, repo.ensureToken())
        if (repo.ensureToken()) {
            requestTargets.forEach { req ->
                scope.launch {
                    val now = ZonedDateTime.now()
                    val last = prefs.getLastFetchEpochMillis(context)
                    val canFetch = prefs.shouldFetch(now, last)

                    if (canFetch || prefs.getDay(context) == null) {
                        val day = repo.getCurrentJournalDay()
                        if (day != null) prefs.setDay(context, day)
                        prefs.setLastFetchNow(context, System.currentTimeMillis())
                    }

                    SmartspacerTargetProvider.notifyChange(context, LessonsTarget::class.java, req.smartspacerId)
                }
            }
        } else {
            requestTargets.forEach { req ->
                SmartspacerTargetProvider.notifyChange(context, LessonsTarget::class.java, req.smartspacerId)
            }
        }
    }
}
