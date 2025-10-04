package com.hansholz.bestenotenapp.smartspacer.requirement

import android.content.Intent
import android.graphics.drawable.Icon
import com.hansholz.bestenotenapp.R
import com.hansholz.bestenotenapp.smartspacer.SmartspacerPrefs
import com.hansholz.bestenotenapp.utils.SimpleTime
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerRequirementProvider

class SchooltimeRequirement : SmartspacerRequirementProvider() {
    override fun getConfig(smartspacerId: String?): Config =
        Config(
            label = "Während der Schulzeit",
            description = "Nur anzeigen, wenn aktuell Schule ist (Optional mit Puffer)",
            icon = Icon.createWithResource(provideContext(), R.drawable.logo_monochrome),
            configActivity =
                Intent(
                    provideContext(),
                    SchooltimeRequirementConfigActivity::class.java,
                ),
        )

    override fun isRequirementMet(smartspacerId: String): Boolean {
        val ctx = provideContext()
        val day = SmartspacerPrefs.getDay(ctx) ?: return false

        val startTime = day.lessons?.minOfOrNull { SimpleTime.parse(it.time?.from ?: "23:59") } ?: return false
        val endTime = day.lessons.maxOfOrNull { SimpleTime.parse(it.time?.to ?: "00:00") } ?: return false

        val before = SchooltimeRequirementSettings.getPaddingBefore(ctx, smartspacerId)
        val after = SchooltimeRequirementSettings.getPaddingAfter(ctx, smartspacerId)

        val requirementStart = startTime.minus(before)
        val requirementEnd = endTime.plus(after)

        val now = SimpleTime.now()

        return now >= requirementStart && now <= requirementEnd
    }
}
