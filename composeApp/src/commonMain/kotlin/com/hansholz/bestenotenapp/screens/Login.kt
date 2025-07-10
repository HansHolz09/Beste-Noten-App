package com.hansholz.bestenotenapp.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.hansholz.bestenotenapp.components.enhancedHazeEffect
import com.hansholz.bestenotenapp.main.LocalTitleBarModifier
import com.hansholz.bestenotenapp.main.Platform
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.main.getPlatform
import com.hansholz.bestenotenapp.theme.FontFamilies
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
                    Text("Login", fontFamily = FontFamilies.KeaniaOne(), maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                modifier = LocalTitleBarModifier.current.customTitleBarMouseEventHandler(),
                colors = TopAppBarDefaults.topAppBarColors(Color.Transparent)
            )
        },
        containerColor = Color.Transparent,
        content = { innerPadding ->
            BoxWithConstraints(
                modifier = Modifier.padding(innerPadding).imePadding().fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                val width = this.maxWidth
                val modifier = if (width >= 780.dp) Modifier.width(400.dp) else Modifier.fillMaxWidth()
                AnimatedContent(
                    targetState = isLoading,
                    modifier = Modifier.align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    if (it) {
                        ContainedLoadingIndicator()
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            var stayLoggedIn by remember { mutableStateOf(false) }

                            Text(
                                text = "Melde dich mit deinem Account von beste.schule an",
                                modifier = modifier,
                                fontFamily = FontFamilies.Schoolbell(),
                                textAlign = TextAlign.Center,
                                style = typography.headlineMedium
                            )
                            Spacer(Modifier.height(30.dp))
                            val textFieldState = rememberTextFieldState()
                            suspend fun loginUsingPrivateAccessToken() {
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
                                    isLoading = false
                                }
                            }
                            OutlinedTextField(
                                state = textFieldState,
                                modifier = modifier.widthIn(max = 500.dp),
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
                                                loginUsingPrivateAccessToken()
                                            }
                                        }
                                    ) {
                                        Icon(Icons.AutoMirrored.Outlined.Login, null)
                                    }
                                },
                                placeholder = { Text("Private-Access-Token") },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                onKeyboardAction = KeyboardActionHandler {
                                    scope.launch {
                                        loginUsingPrivateAccessToken()
                                    }
                                },
                                lineLimits = TextFieldLineLimits.SingleLine
                            )
                            Text("oder")
                            Button(
                                onClick = {
                                    scope.launch {
                                        isLoading = true
                                        if (viewModel.getAccessToken()) {
                                            viewModel.init()
                                            if (stayLoggedIn) {
                                                viewModel.stayLoggedIn()
                                            }
                                            onNavigateHome()
                                        } else {
                                            isLoading = false
                                        }
                                    }
                                },
                                modifier = modifier,
                                enabled = getPlatform() != Platform.WEB
                            ) {
                                Text("Login über beste.schule")
                            }
                            HorizontalDivider(modifier.padding(top = 10.dp))
                            Row(
                                modifier = modifier,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Angemeldet bleiben")
                                Checkbox(
                                    checked = stayLoggedIn,
                                    onCheckedChange = { stayLoggedIn = it }
                                )
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