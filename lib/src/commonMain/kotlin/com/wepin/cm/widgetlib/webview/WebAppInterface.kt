package com.wepin.cm.widgetlib.webview

expect class WebAppInterface {
    companion object {
        fun getInstance(): WebAppInterface
    }
    fun openInAppBrowser(url: String)
}