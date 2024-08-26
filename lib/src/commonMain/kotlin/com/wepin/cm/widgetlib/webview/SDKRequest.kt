package com.wepin.cm.widgetlib.webview

import com.wepin.cm.widgetlib.types.JSRequest
import com.wepin.cm.widgetlib.types.JSRequestBody
import com.wepin.cm.widgetlib.types.JSRequestHeader
import io.ktor.util.date.getTimeMillis
import kotlin.random.Random

object SDKRequest {
    private var _request: JSRequest? = null

    fun getSDKRequestHeader():JSRequestHeader? {
        return _request?.header
    }

    fun getSDKRequestBody():JSRequestBody? {
        return _request?.body
    }

    fun setRequest(command: String, parameter: Any) {
        _request = JSRequest(
            JSRequestHeader(
                id = getTimeMillis() + Random.nextLong(0, 1000),
                request_to = "wepin_widget",
                request_from = "compose-multiplatform"
            ),
            JSRequestBody(
                command = command,
                parameter = parameter
            )
        )
    }

    fun setNullRequest() {
        _request = null
    }
}