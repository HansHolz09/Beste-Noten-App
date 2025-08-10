package com.hansholz.bestenotenapp.smartspacer.requirement


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.components.TopAppBarScaffold
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedOutlinedButton
import com.hansholz.bestenotenapp.theme.AppTheme
import com.kieronquinn.app.smartspacer.sdk.SmartspacerConstants
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerRequirementProvider
import kotlin.math.roundToInt

class SchooltimeRequirementConfigActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val smartspacerId = intent.getStringExtra(SmartspacerConstants.EXTRA_SMARTSPACER_ID)
            ?: run {
                setResult(RESULT_CANCELED)
                finish()
                return
            }

        setContent {
            AppTheme {
                ConfigScreen(
                    initialBefore = SchooltimeRequirementSettings.getPaddingBefore(this, smartspacerId),
                    initialAfter = SchooltimeRequirementSettings.getPaddingAfter(this, smartspacerId),
                    onSave = { before, after ->
                        SchooltimeRequirementSettings.setPaddingBefore(this, smartspacerId, before)
                        SchooltimeRequirementSettings.setPaddingAfter(this, smartspacerId, after)
                        SmartspacerRequirementProvider.notifyChange(
                            this,
                            SchooltimeRequirement::class.java,
                            smartspacerId
                        )
                        setResult(RESULT_OK)
                        finish()
                    },
                    onCancel = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
private fun ConfigScreen(
    initialBefore: Int,
    initialAfter: Int,
    onSave: (before: Int, after: Int) -> Unit,
    onCancel: () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current

    var before by remember { mutableStateOf(initialBefore.toFloat()) }
    var after by remember { mutableStateOf(initialAfter.toFloat()) }

    TopAppBarScaffold(
        title = "Schulzeit-Bedingung",
    ) { innerPadding, topAppBarBackground ->
        topAppBarBackground(innerPadding.calculateTopPadding())
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            Text(
                text = "Lege einen Puffer für vor bzw. nach der Schulzeit fest. Innerhalb dieser erweiterten Schulzeit gilt die Bedingung ebenfalls als erfüllt.",
                textAlign = TextAlign.Justify,
            )

            Text("Puffer davor: ${before.toInt()} min", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = before,
                onValueChange = {
                    before = it
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                },
                valueRange = 0f..60f,
                steps = 59,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Puffer danach: ${after.toInt()} min", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = after,
                onValueChange = {
                    after = it
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                },
                valueRange = 0f..60f,
                steps = 59,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                EnhancedButton(onClick = {
                    onSave(before.roundToInt(), after.roundToInt())
                }) { Text("Speichern") }
                Spacer(Modifier.width(12.dp))
                EnhancedOutlinedButton(onClick = onCancel) { Text("Abbrechen") }
            }
        }
    }
}