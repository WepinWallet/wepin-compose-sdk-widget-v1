package com.wepin.cm.widgetlib.webview

import com.wepin.cm.widgetlib.types.JSResponse
import kotlinx.coroutines.CompletableDeferred

sealed class PinAuthResponse {
    data class Data(val value: JSResponse.JSResponseBody.JSPinAuthResponseBodyData) : PinAuthResponse()
    data class StringValue(val value: String) : PinAuthResponse()
}

object WebViewResponseManager {
    var registerDeferred: CompletableDeferred<JSResponse.JSResponseBody.JSRegisterResponseBodyData>? = null
    var sendDeferred: CompletableDeferred<String>? = null
    var receiveDeferred: CompletableDeferred<String>? = null
    //var pinDeferred: CompletableDeferred<JSResponse.JSResponseBody.JSPinAuthResponseBodyData>? = null
    var pinDeferred: CompletableDeferred<PinAuthResponse>? = null
    var loginDeferred: CompletableDeferred<Boolean>? = null
    var viewAccountDetailDeferred: CompletableDeferred<String>? = null
}