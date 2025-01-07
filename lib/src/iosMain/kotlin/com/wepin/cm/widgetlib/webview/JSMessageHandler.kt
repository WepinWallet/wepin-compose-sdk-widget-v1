package com.wepin.cm.widgetlib.webview

import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.jsbridge.dataToJsonString
import com.multiplatform.webview.jsbridge.processParams
import com.wepin.cm.widgetlib.types.Command
import com.wepin.cm.widgetlib.types.JSRequest
import com.wepin.cm.widgetlib.types.JSResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.WebKit.WKScriptMessage
import platform.WebKit.WKScriptMessageHandlerProtocol
import platform.WebKit.WKUserContentController
import platform.darwin.NSObject

class IOSJSMessageHandler():
    NSObject(),
    WKScriptMessageHandlerProtocol {
    override fun userContentController(
        userContentController: WKUserContentController,
        didReceiveScriptMessage: WKScriptMessage
    ) {
        val body = didReceiveScriptMessage.body
        val method = didReceiveScriptMessage.name
        (body as String).apply {
            val message = Json.decodeFromString<JsMessage>(body)
            when (message.methodName) {
                "wepin-compose-request" -> {
                    val param = Json.decodeFromString<JSRequest>(message.params)
                    val data = JSRequest(param.header, param.body)
                    val requestMessageHandler = NativeRequestProcessor()
                    if (data.body.command == Command.CMD_GET_LOGIN_INFO) {
                        CoroutineScope(Dispatchers.Main).launch {
                            val response = requestMessageHandler.getLoginInfo(data)
                            if (response !== null)
                                sendCallbackToJs(message.callbackId, Json.encodeToString(response))
                        }
                    } else {
                        val response = requestMessageHandler.dispatcher(0, data)
                        if (response !== null) {
                            sendCallbackToJs(message.callbackId, Json.encodeToString(response))
                        }
                    }
//                    val response = requestMessageHandler.dispatcher(0, data)

                }
                "wepin-compose-response" -> {
                    val param = Json.decodeFromString<JSResponse>(message.params)
                    val data = JSResponse(param.header, param.body)
                    val responseMessageHandler = NativeResponseProcessor()
                    responseMessageHandler.dispatcher(0, data)
                }
            }
        }
    }

    // JavaScript로 콜백 전달하는 함수
    private fun sendCallbackToJs(callbackId: Int, data: String) {
        val escapedData = data.replace("\"", "\\\"")
        val jsCallbackScript = """
            window.kmpJsBridge.onCallback($callbackId, "$escapedData");
        """.trimIndent()

        WebViewManager.getInstance()._webview?.evaluateJavaScript(jsCallbackScript, null)
    }
}