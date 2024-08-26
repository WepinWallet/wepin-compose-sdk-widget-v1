package com.wepin.cm.widgetlib.webview

import com.multiplatform.webview.web.NativeWebView

expect class WebViewManager {
    companion object {
        fun getInstance(): WebViewManager
    }

    fun openWidget()

    fun closeWidget()
    fun destroyWebview(webview: NativeWebView)
    fun createWebView(webview: NativeWebView)
}