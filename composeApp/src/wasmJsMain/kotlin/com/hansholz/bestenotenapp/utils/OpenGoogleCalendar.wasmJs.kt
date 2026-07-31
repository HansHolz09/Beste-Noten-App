package com.hansholz.bestenotenapp.utils

import kotlinx.browser.window

actual fun openGoogleCalendar() {
    window.open("https://calendar.google.com/calendar/r", "_blank", "")
}
