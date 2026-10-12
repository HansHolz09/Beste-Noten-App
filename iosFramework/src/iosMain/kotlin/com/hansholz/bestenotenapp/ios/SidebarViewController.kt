package com.hansholz.bestenotenapp.ios

import platform.UIKit.UIViewController
import com.hansholz.bestenotenapp.sidebarViewController as appSidebarViewController

public fun sidebarViewController(nativeBridge: NativeComponentBridge): UIViewController =
    appSidebarViewController(nativeBridge.appBridge)
