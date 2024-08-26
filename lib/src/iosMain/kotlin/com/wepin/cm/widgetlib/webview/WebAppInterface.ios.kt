package com.wepin.cm.widgetlib.webview

import platform.Foundation.*
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIApplication
import platform.UIKit.UINavigationController
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.WebKit.WKWebView
import platform.darwin.NSObject

actual class WebAppInterface {
    actual companion object {
        var _instance: WebAppInterface? = null
        actual fun getInstance(): WebAppInterface {
            if (_instance == null) {
                _instance = WebAppInterface()
            }
            return _instance as WebAppInterface
        }
    }

    actual fun openInAppBrowser(url: String) {
    }
}