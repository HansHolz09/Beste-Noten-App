package com.hansholz.bestenotenapp.reviews

import platform.Foundation.NSURL
import platform.StoreKit.SKStoreReviewController
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationState
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene

internal actual object StoreReview {
    actual val isSupported = true

    actual fun requestReview(): Boolean {
        val application = UIApplication.sharedApplication
        if (application.applicationState != UIApplicationState.UIApplicationStateActive) return false
        val window =
            application.connectedScenes
                .filterIsInstance<UIWindowScene>()
                .filter { it.activationState == UISceneActivationStateForegroundActive }
                .flatMap { it.windows.filterIsInstance<UIWindow>() }
                .firstOrNull { it.isKeyWindow() } ?: return false
        val root = window.rootViewController ?: return false
        if (root.presentedViewController != null || root.isBeingDismissed() || root.isBeingPresented()) return false
        val scene = window.windowScene ?: return false
        SKStoreReviewController.requestReviewInScene(scene)
        return true
    }

    actual fun openReviewPage() {
        UIApplication.sharedApplication.openURL(
            url = NSURL(string = "https://apps.apple.com/app/id6816709897?action=write-review"),
            options = emptyMap<Any?, Any>(),
            completionHandler = null,
        )
    }
}
