package com.wepin.cm.widgetlib.webview

import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.dataToJsonString
import com.multiplatform.webview.jsbridge.processParams
import com.multiplatform.webview.web.WebViewNavigator
import com.wepin.cm.widgetlib.types.Command
import com.wepin.cm.widgetlib.types.JSRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JSMessageHandler: IJsMessageHandler {
    override fun handle(
        message: JsMessage,
        navigator: WebViewNavigator?,
        callback: (String) -> Unit
    ) {
        try {
            val param = processParams<JSRequest>(message)
            val data = JSRequest(param.header, param.body)
            val requestMessageHandler = NativeRequestProcessor()

            if (data.body.command == Command.CMD_GET_LOGIN_INFO) {
                CoroutineScope(Dispatchers.Main).launch {
                    val response = requestMessageHandler.getLoginInfo(data)
                    if (response !== null)
                        callback(dataToJsonString(response))
                }
            } else {
                val response = requestMessageHandler.dispatcher(0, data)
                if (response !== null) {
                    callback(dataToJsonString(response))
                }
            }
        } catch(error: Exception) {
            throw error
        }
    }

    override fun methodName(): String {
        return "wepin-compose-request"
    }
}