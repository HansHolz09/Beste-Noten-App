package com.hansholz.bestenotenapp.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import bestenotenapp.composeapp.generated.resources.Res
import bestenotenapp.composeapp.generated.resources.logo
import com.hansholz.bestenotenapp.components.CurvedText
import com.hansholz.bestenotenapp.components.enhancedHazeEffect
import com.hansholz.bestenotenapp.components.rotateForever
import com.hansholz.bestenotenapp.main.LocalTitleBarModifier
import com.hansholz.bestenotenapp.main.Platform
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.main.getPlatform
import com.hansholz.bestenotenapp.theme.FontFamilies
import com.hansholz.bestenotenapp.theme.LocalAnimationsEnabled
import com.hansholz.bestenotenapp.utils.customTitleBarMouseEventHandler
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Login(
    viewModel: ViewModel,
    onNavigateHome: () -> Unit,
) {
    val scope = viewModel.viewModelScope
    @Suppress("DEPRECATION")
    val clipboard = LocalClipboardManager.current
    val animationsEnabled by LocalAnimationsEnabled.current

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
                modifier = Modifier.fillMaxSize().hazeSource(viewModel.hazeBackgroundState),
                contentAlignment = Alignment.Center,
            ) {
                val width = this.maxWidth
                val modifier = if (width >= 780.dp) Modifier.width(400.dp) else Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                AnimatedContent(
                    targetState = isLoading,
                    modifier = Modifier.align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) { targetState ->
                    if (targetState) {
                        ContainedLoadingIndicator()
                    } else {
                        Box(Modifier.verticalScroll(rememberScrollState())) {
                            Column(
                                modifier = Modifier.padding(vertical = 20.dp).padding(innerPadding).consumeWindowInsets(innerPadding).imePadding(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                var stayLoggedIn by remember { mutableStateOf(false) }

                                Box(contentAlignment = Alignment.Center) {
                                    Image(
                                        painter = painterResource(Res.drawable.logo),
                                        contentDescription = null,
                                        modifier = Modifier.size(150.dp)
                                    )
                                    CurvedText(
                                        text = "Willkommen bei der Beste-Noten-App",
                                        radius = 100.dp,
                                        modifier = Modifier.size(225.dp).rotateForever(10000, false, animationsEnabled),
                                        fontSize = 30.sp,
                                        fontFamily = FontFamilies.KeaniaOne(),
                                        startAngle = 110f,
                                        sweepAngle = 315f
                                    )
                                }
                                Spacer(Modifier.height(30.dp))
                                Text(
                                    text = "Melde dich mit deinem Account von beste.schule an",
                                    modifier = modifier,
                                    fontFamily = FontFamilies.Schoolbell(),
                                    textAlign = TextAlign.Center,
                                    style = typography.headlineMedium
                                )
                                Spacer(Modifier.height(30.dp))
                                val textFieldState = rememberTextFieldState()
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
                                                    viewModel.login(
                                                        stayLoggedIn = stayLoggedIn,
                                                        isLoading = { isLoading = it },
                                                        onNavigateHome = onNavigateHome
                                                    ) {
                                                        viewModel.authToken.value = textFieldState.text.toString()
                                                    }
                                                }
                                            },
                                            enabled = textFieldState.text.isNotEmpty()
                                        ) {
                                            Icon(Icons.AutoMirrored.Outlined.Login, null)
                                        }
                                    },
                                    placeholder = { Text("Private-Access-Token") },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    onKeyboardAction = KeyboardActionHandler {
                                        scope.launch {
                                            viewModel.login(
                                                stayLoggedIn = stayLoggedIn,
                                                isLoading = { isLoading = it },
                                                onNavigateHome = onNavigateHome
                                            ) {
                                                viewModel.authToken.value = textFieldState.text.toString()
                                            }
                                        }
                                    },
                                    lineLimits = TextFieldLineLimits.SingleLine
                                )
                                Text("oder")
                                Button(
                                    onClick = {
                                        scope.launch {
                                            viewModel.login(
                                                stayLoggedIn = stayLoggedIn,
                                                isLoading = { isLoading = it },
                                                onNavigateHome = onNavigateHome
                                            ) {
                                                val successful = viewModel.getAccessToken()
                                                if (!successful) error("Could not get Token")
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
            }

            Box(Modifier
                .fillMaxWidth()
                .height(innerPadding.calculateTopPadding())
                .enhancedHazeEffect(viewModel.hazeBackgroundState, colorScheme.secondaryContainer)
            )
        }
    )
}