package com.hansholz.bestenotenapp.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenURLOptionUniversalLinksOnly

actual fun openGoogleCalendar() {
    val app = UIApplication.sharedApplication
    val universalLink = NSURL(string = "https://calendar.google.com/calendar/r")

    app.openURL(
        url = universalLink,
        options = mapOf(UIApplicationOpenURLOptionUniversalLinksOnly to true),
        completionHandler = { success ->
            if (!success) {
                openAppStore(app, "909319292")
            }
        },
    )
}

private fun openAppStore(
    app: UIApplication,
    appId: String,
) {
    val appStoreUrl = NSURL(string = "https://apps.apple.com/app/id$appId")
    app.openURL(
        url = appStoreUrl,
        options = emptyMap<Any?, Any>(),
        completionHandler = null,
    )
}
