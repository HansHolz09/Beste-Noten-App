package com.hansholz.bestenotenapp.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.hansholz.bestenotenapp.components.enhancedHazeEffect
import com.hansholz.bestenotenapp.main.LocalTitleBarModifier
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.utils.customTitleBarMouseEventHandler
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Login(
    viewModel: ViewModel,
    onNavigateHome: () -> Unit,
) {
    val scope = viewModel.viewModelScope
    @Suppress("DEPRECATION")
    val clipboard = LocalClipboardManager.current

    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Login", fontFamily = FontFamily.Serif, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                modifier = LocalTitleBarModifier.current.customTitleBarMouseEventHandler(),
                colors = TopAppBarDefaults.topAppBarColors(Color.Transparent)
            )
        },
        containerColor = Color.Transparent,
        content = { innerPadding ->

            Box(Modifier.padding(innerPadding).fillMaxSize()) {
                AnimatedContent(isLoading, Modifier.align(Alignment.Center)) {
                    if (it) {
                        ContainedLoadingIndicator()
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            var stayLoggedIn by remember { mutableStateOf(false) }
                            val textFieldState = rememberTextFieldState()
                            OutlinedTextField(
                                state = textFieldState,
                                modifier = Modifier.widthIn(max = 500.dp),
                                leadingIcon = {
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                textFieldState.setTextAndPlaceCursorAtEnd(clipboard.getText()?.text ?: "")
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Outlined.ContentPaste, null)
                                    }
                                },
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                isLoading = true
                                                viewModel.authToken.value = textFieldState.text.toString()
                                                try {
                                                    viewModel.api.userMe().data

                                                    viewModel.init()

                                                    if (stayLoggedIn) {
                                                        viewModel.stayLoggedIn()
                                                    }

                                                    onNavigateHome()
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                                isLoading = false
                                            }
                                        }
                                    ) {
                                        Icon(Icons.AutoMirrored.Outlined.Login, null)
                                    }
                                },
                                placeholder = { Text("Private-Access-Token") },
                                lineLimits = TextFieldLineLimits.SingleLine
                            )
                            Button(
                                onClick = {
                                    scope.launch {
                                        isLoading = true
                                        if (viewModel.getAccessToken()) {
                                            viewModel.init()
                                            if (stayLoggedIn) {
                                                viewModel.stayLoggedIn()
                                            }

                                            println(viewModel.authToken.value)
                                            println(viewModel.api.userMe().data.students?.firstOrNull()?.forename ?: "du")

                                            onNavigateHome()
                                        }
                                        isLoading = false
                                    }
                                }
                            ) {
                                Text("Login über beste.schule")
                            }
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = stayLoggedIn,
                                    onCheckedChange = { stayLoggedIn = it }
                                )
                                Text("Angemeldet bleiben")
                            }
                        }
                    }
                }
            }

            Box(Modifier
                .fillMaxWidth()
                .height(innerPadding.calculateTopPadding())
                .enhancedHazeEffect(viewModel.hazeBackgroundState, colorScheme.secondaryContainer)
            )
        }
    )
}