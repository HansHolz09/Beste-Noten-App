package com.hansholz.bestenotenapp.ios

import com.hansholz.bestenotenapp.notifications.ensureIosNotificationsInitialized as appEnsureIosNotificationsInitialized
import com.hansholz.bestenotenapp.notifications.onIosNotificationsBackgrounded as appOnIosNotificationsBackgrounded
import com.hansholz.bestenotenapp.notifications.onIosNotificationsForegrounded as appOnIosNotificationsForegrounded

public fun ensureIosNotificationsInitialized(logError: (String) -> Unit): Unit = appEnsureIosNotificationsInitialized(logError)

public fun onIosNotificationsForegrounded(): Unit = appOnIosNotificationsForegrounded()

public fun onIosNotificationsBackgrounded(): Unit = appOnIosNotificationsBackgrounded()
