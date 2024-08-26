package com.wepin.cm.widgetlib.webview

import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.processParams
import com.multiplatform.webview.web.WebViewNavigator
import kotlinx.serialization.Serializable

class OpenInBrowserHandler: IJsMessageHandler {
    override fun handle(
        message: JsMessage,
        navigator: WebViewNavigator?,
        callback: (String) -> Unit
    ) {
        val param = processParams<URLModel>(message)
        val data = URLModel(param.url)
        WebAppInterface.getInstance().openInAppBrowser(data.url)
    }

    override fun methodName(): String {
        return "open-in-app"
    }
}

@Serializable
data class URLModel(val url: String)