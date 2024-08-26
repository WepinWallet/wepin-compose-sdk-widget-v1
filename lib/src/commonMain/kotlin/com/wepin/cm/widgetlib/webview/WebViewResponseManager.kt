package com.wepin.cm.widgetlib.webview

import com.wepin.cm.widgetlib.types.JSResponse
import kotlinx.coroutines.CompletableDeferred

object WebViewResponseManager {
    var registerDeferred: CompletableDeferred<JSResponse.JSResponseBody.JSRegisterResponseBodyData>? = null
    var sendDeferred: CompletableDeferred<String>? = null
}