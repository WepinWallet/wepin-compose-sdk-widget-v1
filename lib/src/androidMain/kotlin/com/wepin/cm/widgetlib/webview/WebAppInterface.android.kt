package com.wepin.cm.widgetlib.webview

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.wepin.cm.widgetlib.storage.AppData

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
        val context = AppData.getContext() as Context

        val customTabsIntent = CustomTabsIntent.Builder()
            .build()

        // Custom Tabs로 URL 열기
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }
}