package com.wepin.cm.widgetlib.webview

import com.wepin.cm.widgetlib.error.WepinError
import com.wepin.cm.widgetlib.types.Command
import com.wepin.cm.widgetlib.types.ErrorCode
import com.wepin.cm.widgetlib.types.JSResponse

class NativeResponseProcessor {
    fun dispatcher(requestCode: Int, response: JSResponse) {
        val command: String = response.body.command
        SDKRequest.setNullRequest()

        when (command) {
            Command.CMD_WEPIN_REGISTER -> {
                if (response.body.state != "SUCCESS") {
                    WebViewResponseManager.registerDeferred?.completeExceptionally(WepinError.generateExWithMessage(ErrorCode.FAILED_REGISTER, "${response.body.data}"))
                } else {
                    if (response.body.data is JSResponse.JSResponseBody.JSRegisterResponseBodyData) {
                        WebViewResponseManager.registerDeferred?.complete(response.body.data)
                    } else {
                        WebViewResponseManager.registerDeferred?.completeExceptionally(WepinError.generateExWithMessage(ErrorCode.FAILED_REGISTER, "${response.body.data}"))
                    }
                }
            }
            Command.CMD_SEND_TRANSACTION_WITHOUT_PROVIDER -> {
                if (response.body.state != "SUCCESS") {
                    WebViewResponseManager.sendDeferred?.completeExceptionally(WepinError.generateExWithMessage(ErrorCode.FAILED_SEND, "${response.body.data}"))
                } else {
                    if (response.body.data is JSResponse.JSResponseBody.JSStringResponse) {
                        WebViewResponseManager.sendDeferred?.complete(response.body.data.data)
                    } else {
                        WebViewResponseManager.sendDeferred?.completeExceptionally(WepinError.generateExWithMessage(ErrorCode.FAILED_SEND, "${response.body.data}"))
                    }
                }
            }
            Command.CMD_PIN_AUTH -> {
                if (response.body.state != "SUCCESS") {
                    if ((response.body.data as JSResponse.JSResponseBody.JSStringResponse).data == "User Cancel") {
                        WebViewResponseManager.pinDeferred?.complete(PinAuthResponse.StringValue(response.body.data.data))
                    } else {
                        WebViewResponseManager.pinDeferred?.completeExceptionally(WepinError.generateExWithMessage(ErrorCode.FAILED_PIN_VERIFIED, "${response.body.data}"))
                    }
                } else {
                    if (response.body.data is JSResponse.JSResponseBody.JSPinAuthResponseBodyData) {
                        WebViewResponseManager.pinDeferred?.complete(PinAuthResponse.Data(response.body.data))
                    } else {
                        WebViewResponseManager.pinDeferred?.completeExceptionally(WepinError.generalUnKnownEx("${response.body.data}"))
                    }
                }
            }
            Command.CMD_RECEIVE_ACCOUNT -> {
                if (response.body.state != "SUCCESS") {
                    if ((response.body.data as JSResponse.JSResponseBody.JSStringResponse).data == "User Cancel") {
                        WebViewResponseManager.receiveDeferred?.complete(response.body.data.data)
                    } else {
                        WebViewResponseManager.receiveDeferred?.completeExceptionally(WepinError.generateExWithMessage(ErrorCode.FAILED_RECEIVE, "${response.body.data}"))
                    }
                } else {
                    WebViewResponseManager.receiveDeferred?.complete(response.body.state)
                }
            }
            Command.CMD_SHOW_ACCOUNT_DETAIL -> {
                if (response.body.state != "SUCCESS") {
                    if ((response.body.data as JSResponse.JSResponseBody.JSStringResponse).data == "User Cancel") {
                        WebViewResponseManager.viewAccountDetailDeferred?.complete(response.body.data.data)
                    } else {
                        WebViewResponseManager.viewAccountDetailDeferred?.completeExceptionally(WepinError.generateExWithMessage(ErrorCode.FAILED_ACCOUNT_DETAIL, "${response.body.data}"))
                    }
                } else {
                    WebViewResponseManager.viewAccountDetailDeferred?.complete(response.body.state)
                }
            }
            else -> throw Error("It's invalid command")
        }
    }
}