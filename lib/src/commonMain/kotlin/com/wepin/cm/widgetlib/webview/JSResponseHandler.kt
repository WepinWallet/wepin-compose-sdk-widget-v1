package com.wepin.cm.widgetlib.webview

import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.processParams
import com.multiplatform.webview.web.WebViewNavigator
import com.wepin.cm.widgetlib.types.JSResponse

class JSResponseHandler: IJsMessageHandler {
    override fun handle(
        message: JsMessage,
        navigator: WebViewNavigator?,
        callback: (String) -> Unit
    ) {
        try {
            val param = processParams<JSResponse>(message)
            val data = JSResponse(param.header, param.body)
            val responsetMessageHandler = NativeResponseProcessor()
            responsetMessageHandler.dispatcher(0, data)
        } catch(error: Exception) {
            throw error
        }
    }

    override fun methodName(): String {
        return "wepin-compose-response"
    }
}