package com.wepin.cm.widgetlib.webview

import com.wepin.cm.widgetlib.types.JSResponse
import kotlinx.coroutines.CompletableDeferred

object WebViewResponseManager {
    var registerDeferred: CompletableDeferred<JSResponse.JSResponseBody.JSRegisterResponseBodyData>? = null
    var sendDeferred: CompletableDeferred<String>? = null
    var receiveDeferred: CompletableDeferred<String>? = null
    //var pinDeferred: CompletableDeferred<JSResponse.JSResponseBody.JSPinAuthResponseBodyData>? = null
    var pinDeferred: CompletableDeferred<Any>? = null
    var loginDeferred: CompletableDeferred<Boolean>? = null
}