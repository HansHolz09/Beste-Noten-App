package com.hansholz.bestenotenapp.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun openGoogleCalendar() {
    val app = UIApplication.sharedApplication
    val schemeUrl = NSURL(string = "googlecalendar://")

    if (app.canOpenURL(schemeUrl)) {
        app.openURL(schemeUrl)
    } else {
        val appStoreUrl = NSURL(string = "https://apps.apple.com/app/id909319292")
        app.openURL(appStoreUrl)
    }
}
