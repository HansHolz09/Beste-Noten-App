package com.hansholz.bestenotenapp.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.components.PreferenceItem
import com.hansholz.bestenotenapp.components.PreferencePosition
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAlertDialog
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrateN
import com.hansholz.bestenotenapp.components.scrollableEdgeFade
import top.ltfan.multihaptic.compose.rememberVibrator

@Composable
fun ChooseStudentDialog(loginViewModel: LoginViewModel) {
    val vibrator = rememberVibrator()

    var selectedStudent by remember { mutableStateOf("") }
    EnhancedAlertDialog(
        visible = loginViewModel.chooseStudentDialog.first && loginViewModel.chooseStudentDialog.second != null,
        onDismissRequest = {},
        confirmButton = {
            EnhancedButton(
                onClick = {
                    loginViewModel.chosenStudent = selectedStudent
                    loginViewModel.chooseStudentDialog = false to null
                },
                enabled = selectedStudent.isNotEmpty(),
            ) {
                Text("Wählen")
            }
        },
        title = {
            Text(
                text = "Schüler wählen",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        text = {
            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                modifier = Modifier.scrollableEdgeFade(listState),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                val students = loginViewModel.chooseStudentDialog.second.orEmpty()
                students.forEachIndexed { index, student ->
                    item {
                        PreferenceItem(
                            title = "${student.forename} ${student.name}",
                            position =
                                when {
                                    students.size == 1 -> PreferencePosition.Single
                                    index == 0 -> PreferencePosition.Top
                                    index == students.lastIndex -> PreferencePosition.Bottom
                                    else -> PreferencePosition.Middle
                                },
                            onClick = {
                                selectedStudent = student.id.toString()
                                vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                            },
                            trailingContent = {
                                RadioButton(selected = student.id.toString() == selectedStudent, onClick = null)
                            },
                        )
                    }
                }
            }
        },
    )
}
