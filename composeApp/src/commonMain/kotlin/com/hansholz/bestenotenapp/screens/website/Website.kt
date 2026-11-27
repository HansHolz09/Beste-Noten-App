package com.hansholz.bestenotenapp.screens.website

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntSize
import androidx.window.core.layout.WindowSizeClass
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Language
import com.composables.icons.materialsymbols.rounded.Menu
import com.dokar.sonner.Toast
import com.dokar.sonner.ToastType
import com.dokar.sonner.ToasterDefaults
import com.hansholz.bestenotenapp.components.EmptyStateMessage
import com.hansholz.bestenotenapp.components.LocalAppBackgroundCapture
import com.hansholz.bestenotenapp.components.TopAppBarScaffold
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedVisibility
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedIconButton
import com.hansholz.bestenotenapp.components.enhanced.enhancedSharedBounds
import com.hansholz.bestenotenapp.components.enhanced.enhancedSharedElement
import com.hansholz.bestenotenapp.main.LocalBackgroundEnabled
import com.hansholz.bestenotenapp.main.LocalNativeComponentsEnabled
import com.hansholz.bestenotenapp.main.LocalWebsiteBackgroundEnabled
import com.hansholz.bestenotenapp.main.LocalWebsiteDesignEnabled
import com.hansholz.bestenotenapp.main.Platform
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.main.getPlatform
import com.hansholz.bestenotenapp.security.kSafeProvider
import com.hansholz.bestenotenapp.theme.LocalBlurEnabled
import com.hansholz.bestenotenapp.theme.LocalThemeIsDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Website(
    viewModel: ViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClose: () -> Unit,
) = with(sharedTransitionScope) {
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val isCompactWindow =
        !currentWindowAdaptiveInfoV2()
            .windowSizeClass
            .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val colorScheme = MaterialTheme.colorScheme
    val isDark = LocalThemeIsDark.current

    val url =
        remember {
            viewModel.websiteStartUrl.takeIf(BesteSchuleWebsite::isInternalUrl).also { viewModel.websiteStartUrl = null }
                ?: BesteSchuleWebsite.START_URL
        }
    val designEnabledState = LocalWebsiteDesignEnabled.current
    val designEnabled by designEnabledState
    val showBackground = designEnabled && LocalWebsiteBackgroundEnabled.current.value
    val transparent = showBackground && isBesteSchuleWebViewTransparent()
    val backgroundCapture = LocalAppBackgroundCapture.current?.takeIf { showBackground && !transparent && LocalBackgroundEnabled.current.value }
    val blurEnabled by LocalBlurEnabled.current
    var backgroundImage by remember { mutableStateOf<ImageBitmap?>(null) }
    var backgroundDone by remember { mutableStateOf(false) }

    val assets by produceState<BesteSchuleWebAssets?>(null) { value = loadBesteSchuleWebAssets(designEnabled) }

    val transition = animatedVisibilityScope.transition
    val settled = transition.currentState == EnterExitState.Visible && transition.targetState == EnterExitState.Visible
    var wasSettled by remember { mutableStateOf(false) }
    LaunchedEffect(settled) { if (settled) wasSettled = true }
    val showWebView = if (getPlatform() == Platform.DESKTOP) settled else wasSettled

    // Haze can't sample native views, so the drawer blurs a snapshot of the page instead
    val drawerState = viewModel.compactDrawerState.value
    val snapshotForDrawer = isCompactWindow && blurEnabled && !LocalNativeComponentsEnabled.current.value
    var webBridge by remember { mutableStateOf<BesteSchuleWebBridge?>(null) }
    var drawerSnapshot by remember { mutableStateOf<ImageBitmap?>(null) }
    val drawerHidden = drawerState.isClosed && !drawerState.isAnimationRunning
    LaunchedEffect(drawerHidden) { if (drawerHidden) drawerSnapshot = null }

    TopAppBarScaffold(
        modifier =
            if (transition.targetState == EnterExitState.Visible || getPlatform() != Platform.IOS) {
                Modifier.enhancedSharedBounds(
                    sharedTransitionScope = sharedTransitionScope,
                    sharedContentState = rememberSharedContentState(key = "website-card"),
                    animatedVisibilityScope = animatedVisibilityScope,
                )
            } else {
                Modifier
            },
        title = "beste.schule",
        titleModifier =
            Modifier
                .enhancedSharedElement(
                    sharedTransitionScope = sharedTransitionScope,
                    sharedContentState = rememberSharedContentState(key = "website-title"),
                    animatedVisibilityScope = animatedVisibilityScope,
                ).skipToLookaheadSize(),
        navigationIcon = {
            EnhancedIconButton(
                onClick = {
                    scope.launch {
                        if (snapshotForDrawer && drawerState.isClosed) drawerSnapshot = webBridge?.snapshot?.invoke()
                        viewModel.closeOrOpenDrawer(isCompactWindow)
                    }
                },
            ) {
                Icon(
                    imageVector = MaterialSymbols.Rounded.Menu,
                    contentDescription = "Menü",
                )
            }
        },
        sideMenuExpanded = viewModel.mediumExpandedDrawerState.value.isOpen,
        hazeState = viewModel.hazeBackgroundState,
    ) { innerPadding, topAppBarBackground ->
        val contentPadding =
            PaddingValues(
                start = innerPadding.calculateStartPadding(layoutDirection),
                top = innerPadding.calculateTopPadding(),
                end = innerPadding.calculateEndPadding(layoutDirection),
            )
        val bottomPadding = innerPadding.calculateBottomPadding().value
        val theme = { assets: BesteSchuleWebAssets, bottomInset: Float ->
            assets.theme(
                colors = colorScheme,
                dark = isDark,
                design = designEnabled,
                transparent = transparent,
                focused = url != BesteSchuleWebsite.START_URL,
                bottomInset = bottomInset,
            )
        }
        val bridge =
            remember(assets) {
                assets?.let {
                    BesteSchuleWebBridge(
                        scope = scope,
                        tokenProvider = viewModel::getWebsiteAccessToken,
                        onOpenExternal = uriHandler::openUri,
                        onClose = onClose,
                        theme = theme(it, bottomPadding),
                        assets = it,
                    )
                }
            }

        LaunchedEffect(bridge) { webBridge = bridge }

        Box(Modifier.fillMaxSize()) {
            if (bridge != null) {
                val bottomInset = max(bottomPadding, bridge.systemBottomInset)
                LaunchedEffect(bridge, colorScheme, isDark, designEnabled, transparent, bottomInset) {
                    assets?.let { bridge.updateTheme(theme(it, bottomInset)) }
                }
                LaunchedEffect(bridge.problem) {
                    if (bridge.problem != null && designEnabledState.value) {
                        designEnabledState.value = false
                        kSafeProvider(viewModel.kSafe) { put("websiteDesignEnabled", false) }
                        viewModel.toaster.show(
                            Toast(
                                message = "Die Anpassungen der Webseite wurden wegen eines Darstellungsfehlers deaktiviert und lassen sich in den Einstellungen wieder aktivieren.",
                                type = ToastType.Warning,
                                duration = ToasterDefaults.DurationLong,
                            ),
                        )
                    }
                }

                var webViewPosition by remember { mutableStateOf<Offset?>(null) }
                if (backgroundCapture != null) {
                    LaunchedEffect(bridge, colorScheme, isDark, blurEnabled, LocalWindowInfo.current.containerSize) {
                        if (backgroundImage != null) delay(150.milliseconds)
                        withTimeoutOrNull(2.seconds) { backgroundCapture.capture() }?.let {
                            backgroundImage = it
                            bridge.updateBackdrop(besteSchuleBackdropJpeg(it, colorScheme.background))
                        }
                        backgroundDone = true
                    }
                } else {
                    LaunchedEffect(bridge) {
                        bridge.updateBackdrop(null)
                        backgroundImage = null
                        backgroundDone = true
                    }
                }
                LaunchedEffect(bridge, webViewPosition, backgroundImage) {
                    val position = webViewPosition ?: return@LaunchedEffect
                    val image = backgroundImage ?: return@LaunchedEffect
                    bridge.updateBackdropPlacement(
                        BesteSchuleBackdropPlacement(
                            x = position.x / density.density,
                            y = position.y / density.density,
                            width = image.width / density.density,
                            height = image.height / density.density,
                        ),
                    )
                }

                // WebKitGTK leaves its GL context current while mounting, so the cover image has to reach the GPU a frame earlier
                var coverDrawn by remember { mutableStateOf(false) }
                LaunchedEffect(showWebView, backgroundDone) {
                    if (showWebView && backgroundDone) {
                        withFrameNanos {}
                        coverDrawn = true
                    }
                }

                if (showWebView && coverDrawn) {
                    LaunchedEffect(bridge) {
                        delay(15.seconds)
                        bridge.isReady = true
                        bridge.isBackdropShown = true
                    }
                    BesteSchuleWebView(
                        url = url,
                        bridge = bridge,
                        transparent = transparent,
                        backgroundColor = colorScheme.background,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(contentPadding)
                                .onGloballyPositioned { webViewPosition = it.positionInWindow() },
                    )
                    drawerSnapshot?.let { snapshot ->
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(contentPadding)
                                .drawBehind { drawImage(snapshot, dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt())) },
                        )
                    }
                }

                if (!transparent) {
                    val coverAlpha by animateFloatAsState(
                        targetValue =
                            if (bridge.isReady && backgroundDone && bridge.unavailableReason == null &&
                                (backgroundImage == null || bridge.isBackdropShown)
                            ) {
                                0f
                            } else {
                                1f
                            },
                        animationSpec = tween(300),
                    )
                    var coverPosition by remember { mutableStateOf<Offset?>(null) }
                    if (showWebView && coverAlpha > 0f) {
                        val background = colorScheme.background
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(contentPadding)
                                .clipToBounds()
                                .onGloballyPositioned { coverPosition = it.positionInWindow() }
                                .drawBehind {
                                    val image = backgroundImage
                                    val position = coverPosition
                                    if (backgroundDone || image != null) drawRect(background, alpha = coverAlpha)
                                    if (image != null && position != null) drawImage(image, topLeft = -position, alpha = coverAlpha)
                                },
                        )
                    }
                }
            }
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                val unavailableReason = bridge?.unavailableReason
                EnhancedAnimatedVisibility(
                    visible =
                        unavailableReason == null &&
                            if (transparent) {
                                bridge?.isRendered != true
                            } else {
                                bridge?.isReady != true || !backgroundDone || (backgroundImage != null && !bridge.isBackdropShown)
                            },
                    exit = scaleOut() + fadeOut(),
                ) {
                    ContainedLoadingIndicator()
                }
                EnhancedAnimatedVisibility(unavailableReason != null) {
                    EmptyStateMessage(
                        title = "Webseite nicht verfügbar",
                        description = unavailableReason,
                        icon = MaterialSymbols.Rounded.Language,
                        button = {
                            EnhancedButton(onClick = { uriHandler.openUri(url) }) {
                                Text("Im Browser öffnen")
                            }
                        },
                    )
                }
            }
        }
        topAppBarBackground(innerPadding.calculateTopPadding())
    }
}
