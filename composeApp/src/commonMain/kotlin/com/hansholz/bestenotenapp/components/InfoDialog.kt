package com.hansholz.bestenotenapp.components

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.AnnotatedString
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Info
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import components.dialogs.EnhancedAlertDialog

@Composable
fun InfoDialog(
    visible: MutableState<Boolean>,
    title: String,
    message: AnnotatedString,
) {
    EnhancedAlertDialog(
        visible = visible.value,
        onDismissRequest = { visible.value = false },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    visible.value = false
                },
            ) {
                Text("Ok")
            }
        },
        icon = { Icon(MaterialSymbols.Rounded.Info, null) },
        title = { Text(title) },
        text = { Text(message) },
    )
}
