package com.hansholz.bestenotenapp.ios

import platform.UIKit.UIViewController
import com.hansholz.bestenotenapp.easterEggOverlayViewController as appEasterEggOverlayViewController

public fun easterEggOverlayViewController(nativeBridge: NativeComponentBridge): UIViewController =
    appEasterEggOverlayViewController(nativeBridge.appBridge)
