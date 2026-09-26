package com.hansholz.bestenotenapp.updates

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import bestenotenapp.composeApp.BuildConfig
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.System_update_alt
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAlertDialog
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedOutlinedButton
import com.hansholz.bestenotenapp.components.scrollableEdgeFade
import com.hansholz.bestenotenapp.security.kSafeProviderCompose
import com.hansholz.bestenotenapp.utils.IO
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.markdownPadding
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val MAXIMUM_SKIPS = 3

@Composable
fun UpdateDialogHost() =
    kSafeProviderCompose {
        var update by remember { mutableStateOf<AvailableUpdate?>(null) }
        var visible by remember { mutableStateOf(false) }
        var skipCount by remember { mutableIntStateOf(0) }

        LaunchedEffect(Unit) {
            try {
                val availableUpdate = withContext(Dispatchers.IO) { checkForUpdate() } ?: return@LaunchedEffect
                skipCount = get("updateSkipCount_${availableUpdate.version}", 0)
                update = availableUpdate
                visible = true
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        update?.let { availableUpdate ->
            val uriHandler = LocalUriHandler.current

            EnhancedAlertDialog(
                visible = visible,
                onDismissRequest = {},
                maxWidth = 640.dp,
                icon = { Icon(MaterialSymbols.Rounded.System_update_alt, null) },
                title = { Text("Update verfügbar") },
                text = {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .heightIn(max = 520.dp)
                                .scrollableEdgeFade(scrollState)
                                .verticalScroll(scrollState),
                    ) {
                        Text(
                            text = "von ${BuildConfig.VERSION_NAME} zu ${availableUpdate.version}\n${availableUpdate.size}",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                        Markdown(
                            content = availableUpdate.releaseNotes,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                            padding = markdownPadding(list = 0.dp, listItemTop = 1.dp, listItemBottom = 1.dp),
                            typography =
                                markdownTypography(
                                    h1 = MaterialTheme.typography.headlineLarge,
                                    h2 = MaterialTheme.typography.headlineMedium,
                                    h3 = MaterialTheme.typography.headlineSmall,
                                    h4 = MaterialTheme.typography.titleLarge,
                                    h5 = MaterialTheme.typography.titleMedium,
                                    h6 = MaterialTheme.typography.titleSmall,
                                    text = MaterialTheme.typography.bodyMedium,
                                    code = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                    inlineCode = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                                    quote = MaterialTheme.typography.bodySmall.plus(SpanStyle(fontStyle = FontStyle.Italic)),
                                    paragraph = MaterialTheme.typography.bodyMedium,
                                    ordered = MaterialTheme.typography.bodyMedium,
                                    bullet = MaterialTheme.typography.bodyMedium,
                                    list = MaterialTheme.typography.bodyMedium,
                                    textLink =
                                        TextLinkStyles(
                                            style =
                                                MaterialTheme.typography.bodyMedium
                                                    .copy(
                                                        fontWeight = FontWeight.Bold,
                                                        textDecoration = TextDecoration.Underline,
                                                    ).toSpanStyle(),
                                        ),
                                    table = MaterialTheme.typography.bodyMedium,
                                ),
                        )
                    }
                },
                dismissButton =
                    if (skipCount < MAXIMUM_SKIPS) {
                        {
                            EnhancedOutlinedButton(
                                onClick = {
                                    put("updateSkipCount_${availableUpdate.version}", skipCount + 1)
                                    visible = false
                                },
                            ) {
                                Text("Überspringen (${skipCount + 1}/$MAXIMUM_SKIPS)")
                            }
                        }
                    } else {
                        null
                    },
                confirmButton = {
                    EnhancedButton(
                        onClick = {
                            put("updateSkipCount_${availableUpdate.version}", skipCount + 1)
                            if (skipCount < MAXIMUM_SKIPS) visible = false
                            uriHandler.openUri(availableUpdate.downloadUrl)
                        },
                    ) {
                        Text("Herunterladen")
                    }
                },
            )
        }
    }
