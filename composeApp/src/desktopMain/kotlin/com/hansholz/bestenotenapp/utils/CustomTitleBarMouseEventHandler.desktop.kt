package com.hansholz.bestenotenapp.utils

import com.hansholz.bestenotenapp.decoratedWindow.CustomTitleBarObject

actual fun forceHitTest(boolean: Boolean) {
    CustomTitleBarObject.customTitleBar?.forceHitTest(boolean)
}