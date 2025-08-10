package com.hansholz.bestenotenapp.smartspacer.target

import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Icon
import com.hansholz.bestenotenapp.MainActivity
import com.hansholz.bestenotenapp.R
import com.hansholz.bestenotenapp.api.models.JournalDay
import com.hansholz.bestenotenapp.api.models.JournalLesson
import com.hansholz.bestenotenapp.smartspacer.SmartspacerPrefs
import com.hansholz.bestenotenapp.utils.SimpleTime
import com.kieronquinn.app.smartspacer.sdk.model.CompatibilityState
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import kotlin.time.ExperimentalTime

class LessonsTarget: SmartspacerTargetProvider() {
    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        val isTokenStored = SmartspacerPrefs.getTokenState(provideContext())
        val day = SmartspacerPrefs.getDay(provideContext())
        val (title, subtitle) = if (!isTokenStored) {
            "Nutze \"Angemeldet bleiben\" in der Beste-Noten-App" to "Die Aktualisierung dauert einen Moment"
        } else if (day != null) {
            val (current, next) = day.nowAndNext()
            val t = current?.let { "Aktuell: ${it.prettyLine()}" } ?: "Aktuell: kein Unterricht"
            val s = next?.let { "Als nächstes: ${it.prettyLine()}" } ?: "Als nächstes: kein Unterricht"
            t to s
        } else {
            "Aktuell: —" to "Als nächstes: —"
        }

        val target = TargetTemplate.Basic(
            id = "lessons_$smartspacerId",
            componentName = ComponentName(provideContext(), LessonsTarget::class.java),
            title = Text(title),
            subtitle = Text(subtitle),
            icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                Icon.createWithResource(provideContext(), R.drawable.logo_monochrome)
            ),
            onClick = TapAction(intent = Intent(provideContext(), MainActivity::class.java))
        ).create()

        return listOf(target)
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Stundeninformation",
            description = "Zeigt die aktuelle und nächste Schulstunde",
            icon = Icon.createWithResource(provideContext(), R.drawable.logo_monochrome),
            refreshPeriodMinutes = 1,
            refreshIfNotVisible = true,
            compatibilityState = CompatibilityState.Compatible,
        )
    }

    override fun onDismiss(smartspacerId: String, targetId: String): Boolean = true


    data class NowNext(val current: JournalLesson?, val next: JournalLesson?)

    @OptIn(ExperimentalTime::class)
    fun JournalDay.nowAndNext(): NowNext {
        val now = SimpleTime.now()

        val all = lessons?.sortedBy { SimpleTime.parse(it.time?.from ?: "00:00") }

        val current = all?.firstOrNull { now >= SimpleTime.parse(it.time?.from ?: "00:00") && now < SimpleTime.parse(it.time?.to ?: "00:00") }
        val next = all?.firstOrNull { SimpleTime.parse(it.time?.from ?: "00:00") > now }

        return NowNext(current, next)
    }

    fun JournalLesson.prettyLine(): String {
        val subjectName = (if ((subject?.name?.length ?: 0) <= 10) subject?.name else subject?.localId) ?: "?"
        return if (status == "canceled") {
            "Ausfall ($subjectName)"
        } else {
            "$subjectName in ${rooms?.joinToString { it.localId } ?: "?"}" +
                    if (!notes.isNullOrEmpty()) " - " + notes.joinToString(" - ") { it.type?.name ?: "" } else ""
        }
    }
}