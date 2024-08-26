package com.wepin.cm.widgetlib.webview

import com.wepin.cm.widgetlib.types.Command
import com.wepin.cm.widgetlib.types.JSResponse

class NativeResponseProcessor {
    fun dispatcher(requestCode: Int, response: JSResponse) {
        val command: String = response.body.command
        SDKRequest.setNullRequest()

        when (command) {
            Command.CMD_WEPIN_REGISTER -> {
                if (response.body.state != "SUCCESS") {
                    WebViewResponseManager.registerDeferred?.completeExceptionally(Exception("${response.body.data}"))
                } else {
                    WebViewResponseManager.registerDeferred?.complete(response.body.data as JSResponse.JSResponseBody.JSRegisterResponseBodyData)
                }
            }
            Command.CMD_SEND_TRANSACTION_WITHOUT_PROVIDER -> {
                if (response.body.state != "SUCCESS") {
                    WebViewResponseManager.sendDeferred?.completeExceptionally(Exception("${response.body.data}"))
                } else {
                    WebViewResponseManager.sendDeferred?.complete((response.body.data as JSResponse.JSResponseBody.JSStringResponse).data)
                }
            }
            else -> throw Error("")
        }
    }
}