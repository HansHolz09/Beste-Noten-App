package com.hansholz.bestenotenapp.ios

import com.hansholz.bestenotenapp.NativeComponentBridge as AppNativeComponentBridge

public class NativeComponentBridge {
    internal val appBridge = AppNativeComponentBridge()

    public fun setCallbacks(
        onRootDestinationChanged: (String) -> Unit,
        onFragmentDestinationChanged: (String) -> Unit,
        onCanNavigateBackChanged: (Boolean) -> Unit,
        onThemeChanged: (Boolean, Boolean) -> Unit,
        onPrimaryTabsChanged: (String, Int, Boolean) -> Unit,
        onPrimaryTabsVisibilityChanged: (Double) -> Unit,
        onOfflineStatusChanged: (Boolean) -> Unit,
    ): Unit = appBridge.setCallbacks(
        onRootDestinationChanged,
        onFragmentDestinationChanged,
        onCanNavigateBackChanged,
        onThemeChanged,
        onPrimaryTabsChanged,
        onPrimaryTabsVisibilityChanged,
        onOfflineStatusChanged,
    )

    public fun selectFragment(route: String): Unit = appBridge.selectFragment(route)

    public fun navigateBack(): Boolean = appBridge.navigateBack()

    public fun selectPrimaryTab(index: Int): Unit = appBridge.selectPrimaryTab(index)

    public fun showEasterEgg(type: String): Unit = appBridge.showEasterEgg(type)

    public fun sidebarTopInsetChanged(points: Double): Unit = appBridge.sidebarTopInsetChanged(points)

    public fun contentTopInsetChanged(points: Double): Unit = appBridge.contentTopInsetChanged(points)

    public fun systemAppearanceChanged(isDark: Boolean): Unit = appBridge.systemAppearanceChanged(isDark)
}
