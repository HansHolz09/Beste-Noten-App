package com.hansholz.bestenotenapp.screens.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrate
import components.dialogs.EnhancedAlertDialog
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
            LazyColumn {
                loginViewModel.chooseStudentDialog.second?.forEach { student ->
                    item {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedStudent = student.id.toString()
                                    vibrator.enhancedVibrate(EnhancedVibrations.CLICK)
                                }.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = student.id.toString() == selectedStudent,
                                onClick = null,
                            )
                            Text(
                                text = "${student.forename} ${student.name}",
                                style = typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp),
                            )
                        }
                    }
                }
            }
        },
    )
}
