package com.hansholz.bestenotenapp.ios

import platform.UIKit.UIViewController
import com.hansholz.bestenotenapp.mainViewController as appMainViewController

public fun mainViewController(nativeBridge: NativeComponentBridge? = null): UIViewController =
    appMainViewController(nativeBridge?.appBridge)
