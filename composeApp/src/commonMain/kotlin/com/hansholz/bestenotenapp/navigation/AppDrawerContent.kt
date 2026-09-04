package com.hansholz.bestenotenapp.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hansholz.bestenotenapp.components.cupertinoHighlight
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimated
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedContent
import com.hansholz.bestenotenapp.theme.FontFamilies
import com.nomanr.animate.compose.animated.rememberAnimatedState
import com.nomanr.animate.compose.presets.attentionseekers.Jello
import com.nomanr.animate.compose.presets.attentionseekers.RubberBand
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun AppDrawerContent(
    selectedRoute: String?,
    topPadding: Dp,
    animateLogo: Boolean,
    onDestinationSelected: (Fragment) -> Unit,
    onLogoClick: () -> Unit,
) {
    Column {
        Spacer(Modifier.fillMaxWidth().height(topPadding))
        val animateState = rememberAnimatedState()
        LaunchedEffect(animateLogo) {
            while (animateLogo) {
                animateState.animate()
                delay(5.seconds)
            }
        }
        EnhancedAnimated(
            preset = RubberBand(0.05f),
            durationMillis = 1000,
            state = animateState,
        ) {
            Text(
                text = "Beste-Noten-App",
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 40.dp)
                        .clickable(null, null, onClick = onLogoClick),
                color = colorScheme.onSurface,
                autoSize = TextAutoSize.StepBased(10.sp),
                fontFamily = FontFamilies.KeaniaOne,
                maxLines = 1,
            )
        }
        EnhancedAnimated(
            preset = Jello(),
            durationMillis = 1000,
            state = animateState,
        ) {
            Text(
                text = "für beste.schule",
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 80.dp)
                        .clickable(null, null, onClick = onLogoClick),
                color = colorScheme.onSurface,
                autoSize = TextAutoSize.StepBased(5.sp),
                fontFamily = FontFamilies.Schoolbell,
                maxLines = 1,
            )
        }
        Spacer(Modifier.height(15.dp))
        Fragment.entries.forEach { screen ->
            DrawerItem(
                screen = screen,
                selected = selectedRoute == screen.route,
                onDestinationSelected = onDestinationSelected,
            )
        }
        HorizontalDivider(thickness = 2.dp, color = colorScheme.outline)
        DrawerItem(
            screen = Fragment.Settings,
            selected = selectedRoute == Fragment.Settings.route,
            onDestinationSelected = onDestinationSelected,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DrawerItem(
    screen: Fragment,
    selected: Boolean,
    onDestinationSelected: (Fragment) -> Unit,
) {
    EnhancedAnimatedContent(
        targetState = selected,
        transitionSpec = {
            fadeIn(animationSpec = tween(500))
                .togetherWith(fadeOut(animationSpec = tween(500)))
        },
    ) { isSelected ->
        val interactionSource = remember(screen) { MutableInteractionSource() }
        NavigationDrawerItem(
            label = { Text(screen.label) },
            selected = isSelected,
            onClick = { onDestinationSelected(screen) },
            modifier =
                Modifier
                    .padding(10.dp)
                    .cupertinoHighlight(interactionSource, shapes.extraExtraLarge)
                    .then(
                        if (isSelected) Modifier.border(2.dp, colorScheme.onSurface, shapes.extraExtraLarge) else Modifier,
                    ),
            icon = { Icon(screen.icon, null) },
            colors =
                NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = colorScheme.secondaryContainer.copy(0.7f),
                    unselectedTextColor = colorScheme.onSurface,
                ),
            interactionSource = interactionSource,
        )
    }
}
