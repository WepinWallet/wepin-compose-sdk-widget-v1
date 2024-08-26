package com.wepin.cm.widgetlib.utils

import platform.Foundation.NSBundle

actual fun getDomain(): String {
    return NSBundle.mainBundle.bundleIdentifier ?: ""
}

actual fun getPlatform(): Int {
    return 3
}