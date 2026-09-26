package com.hansholz.bestenotenapp.screens.biometry

import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Lock
import com.hansholz.bestenotenapp.components.EmptyStateMessage
import com.hansholz.bestenotenapp.components.TopAppBarScaffold
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedVisibility
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Biometry(
    viewModel: ViewModel,
    onNavigateToScreen: (Screen) -> Unit,
) {
    val biometryViewModel = viewModel { BiometryViewModel() }
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        biometryViewModel.tryBiometricAuthentication(onNavigateToScreen)
    }

    TopAppBarScaffold(
        title = "Authentifizieren",
        hazeState = viewModel.hazeBackgroundState2,
    ) { innerPadding, topAppBarBackground ->
        EmptyStateMessage(
            title = "Gesperrt",
            button = {
                EnhancedAnimatedVisibility(
                    visible = biometryViewModel.isFailure,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    Column {
                        EnhancedButton(
                            onClick = {
                                biometryViewModel.tryBiometricAuthentication(onNavigateToScreen)
                            },
                            modifier = Modifier.sizeIn(maxWidth = 300.dp).fillMaxWidth().padding(10.dp),
                        ) {
                            Text("Erneut Versuchen")
                        }
                        EnhancedButton(
                            onClick = {
                                scope.launch {
                                    viewModel.logout { uriHandler.openUri("https://beste.schule/me/passport") }
                                    onNavigateToScreen(Screen.Login)
                                }
                            },
                            modifier = Modifier.sizeIn(maxWidth = 300.dp).fillMaxWidth().padding(10.dp),
                        ) {
                            Text("Abmelden")
                        }
                    }
                }
            },
            modifier = Modifier.padding(innerPadding),
            icon = MaterialSymbols.Rounded.Lock,
        )

        topAppBarBackground(innerPadding.calculateTopPadding())
    }
}
