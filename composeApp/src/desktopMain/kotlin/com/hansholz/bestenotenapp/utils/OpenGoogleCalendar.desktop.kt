package com.hansholz.bestenotenapp.utils

import java.awt.Desktop
import java.net.URI

actual fun openGoogleCalendar() {
    Desktop.getDesktop().browse(URI("https://calendar.google.com/calendar/r"))
}
