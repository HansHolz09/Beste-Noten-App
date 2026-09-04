package com.hansholz.bestenotenapp

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavController
import com.hansholz.bestenotenapp.components.hideNativeSwitches
import com.hansholz.bestenotenapp.components.showNativeSwitches

class NativeComponentBridge {
    internal val enabledState = mutableStateOf(true)
    internal val selectedFragmentState = mutableStateOf("home")
    internal val themeColorSchemeState = mutableStateOf<ColorScheme?>(null)
    internal val themeIsDarkState = mutableStateOf(false)
    internal val systemIsDarkState = mutableStateOf(false)
    internal val easterEggState = mutableStateOf<String?>(null)
    internal val sidebarTopInsetState = mutableStateOf(15.0)
    internal val contentTopInsetState = mutableStateOf(0.0)

    private var navController: NavController? = null
    private var rootDestinationChanged: ((String) -> Unit)? = null
    private var fragmentDestinationChanged: ((String) -> Unit)? = null
    private var canNavigateBackChanged: ((Boolean) -> Unit)? = null
    private var themeChanged: ((Boolean, Boolean) -> Unit)? = null
    private var primaryTabsChanged: ((String, Int, Boolean) -> Unit)? = null
    private var primaryTabsOwner: String? = null
    private var primaryTabSelected: ((Int) -> Unit)? = null

    fun setCallbacks(
        onRootDestinationChanged: (String) -> Unit,
        onFragmentDestinationChanged: (String) -> Unit,
        onCanNavigateBackChanged: (Boolean) -> Unit,
        onThemeChanged: (Boolean, Boolean) -> Unit,
        onPrimaryTabsChanged: (String, Int, Boolean) -> Unit,
    ) {
        rootDestinationChanged = onRootDestinationChanged
        fragmentDestinationChanged = onFragmentDestinationChanged
        canNavigateBackChanged = onCanNavigateBackChanged
        themeChanged = onThemeChanged
        primaryTabsChanged = onPrimaryTabsChanged
    }

    fun selectFragment(route: String) {
        selectedFragmentState.value = route
        val controller = navController ?: return
        if (controller.currentDestination?.route == route) return
        controller.navigate(route) {
            launchSingleTop = true
            popUpTo(controller.graph.startDestinationId) {
                saveState = false
            }
            restoreState = false
        }
    }

    fun navigateBack(): Boolean = navController?.popBackStack() ?: false

    fun showEasterEgg(type: String) {
        easterEggState.value = null
        easterEggState.value = type
    }

    fun sidebarTopInsetChanged(points: Double) {
        if (sidebarTopInsetState.value != points) sidebarTopInsetState.value = points
    }

    fun contentTopInsetChanged(points: Double) {
        if (contentTopInsetState.value != points) contentTopInsetState.value = points
    }

    fun systemAppearanceChanged(isDark: Boolean) {
        if (systemIsDarkState.value != isDark) systemIsDarkState.value = isDark
    }

    fun selectPrimaryTab(index: Int) {
        primaryTabSelected?.invoke(index)
    }

    internal fun showPrimaryTabs(
        owner: String,
        labels: List<String>,
        selectedIndex: Int,
        onSelected: (Int) -> Unit,
    ) {
        primaryTabsOwner = owner
        primaryTabSelected = onSelected
        primaryTabsChanged?.invoke(labels.joinToString("\u001F"), selectedIndex, true)
    }

    internal fun hidePrimaryTabs(owner: String) {
        if (primaryTabsOwner != owner) return
        primaryTabsOwner = null
        primaryTabSelected = null
        primaryTabsChanged?.invoke("", 0, false)
    }

    internal fun attach(controller: NavController) {
        navController = controller
    }

    internal fun rootDestinationChanged(route: String) {
        if (route != "main") {
            hideNativeSwitches()
        } else {
            showNativeSwitches()
        }
        rootDestinationChanged?.invoke(route)
    }

    internal fun fragmentDestinationChanged(route: String) {
        selectedFragmentState.value = route
        fragmentDestinationChanged?.invoke(route)
    }

    internal fun canNavigateBackChanged(canNavigateBack: Boolean) {
        canNavigateBackChanged?.invoke(canNavigateBack)
    }

    internal fun updateColorScheme(colorScheme: ColorScheme) {
        themeColorSchemeState.value = colorScheme
    }

    internal fun updateTheme(
        isDark: Boolean,
        usesSystemAppearance: Boolean,
    ) {
        themeIsDarkState.value = isDark
        themeChanged?.invoke(isDark, usesSystemAppearance)
    }
}
