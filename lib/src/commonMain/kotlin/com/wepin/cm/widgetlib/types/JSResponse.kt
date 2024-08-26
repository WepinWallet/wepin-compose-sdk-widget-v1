// commonMain/kotlin/io/wepin/widget/webview/param/JSResponse.kt

package com.wepin.cm.widgetlib.types

import JSReadyToWidgetResponseBodyDataSerializer
import com.wepin.cm.loginlib.storage.StorageManager
import com.wepin.cm.widgetlib.const.Constants
import com.wepin.cm.widgetlib.utils.SealedSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class JSResponse(
    val header: JSResponseHeader,
    val body: JSResponseBody
) {
    @Serializable
    data class JSResponseHeader(
        val response_to: String,
        val response_from: String,
        val id: Long
    )

    @Serializable
    data class JSResponseBody(
        val command: String,
        val state: String?,
        val data: JSResponseBodyData?
    ) {
        @Serializable(with = SealedSerializer::class)
        sealed class JSResponseBodyData

        @Serializable(with = JSReadyToWidgetResponseBodyDataSerializer::class)
        data class JSReadyToWidgetResponseBodyData(
            var appKey: String = "",
            var domain: String = "",
            var platform: Int = 0,
            var attributes: WidgetAttributes = WidgetAttributes(),
            var type: String = "",
            var localDate: Map<String, Any> = mapOf(),
            var version: Int = 0
        ) : JSResponseBodyData()

        @Serializable
        data class JSGoogleLogInResponseBodyData(
            var token: String = ""
        ) : JSResponseBodyData()

        @Serializable
        data class JSGetSdkRequestResponseBodyData(
            var header: JSRequestHeader = JSRequestHeader(id = 0, request_to = "", request_from = ""),
            var body: JSRequestBody = JSRequestBody(command = "", parameter = null)
        ) : JSResponseBodyData()

        @Serializable
        data class JSSetUserEmailResponseBodyData(
            var email: String? = null
        ): JSResponseBodyData()

        @Serializable
        data class JSRegisterResponseBodyData(
            val walletId: String
        ): JSResponseBodyData()

        @Serializable
        data class JSStringResponse(
            val data: String
        ): JSResponseBodyData()
    }

    class Builder(target: String, command: String, state: String?, id: Long) {
        private val response: JSResponse

        init {
            val data: JSResponseBody.JSResponseBodyData? = when (command) {
                Command.CMD_READY_TO_WIDGET -> JSResponseBody.JSReadyToWidgetResponseBodyData()
                Command.CMD_GET_SDK_REQUEST -> JSResponseBody.JSGetSdkRequestResponseBodyData()
                Command.CMD_SET_USER_EMAIL -> JSResponseBody.JSSetUserEmailResponseBodyData()
                else -> null
            }
            val header = JSResponseHeader(id = id, response_from = target, response_to = "wepin-widget")
            response = JSResponse(
                header = header,
                body = JSResponseBody(command, state, data)
            )
        }

        fun setAppKey(appKey: String) = apply {
            (response.body.data as? JSResponseBody.JSReadyToWidgetResponseBodyData)?.appKey = appKey
        }

        fun setDomain(domain: String) = apply {
            (response.body.data as? JSResponseBody.JSReadyToWidgetResponseBodyData)?.domain = domain
        }

        fun setPlatform(platform: Int) = apply {
            (response.body.data as? JSResponseBody.JSReadyToWidgetResponseBodyData)?.platform = platform
        }

        fun setAttributes(attributes: WidgetAttributes) = apply {
            (response.body.data as? JSResponseBody.JSReadyToWidgetResponseBodyData)?.attributes = attributes
        }

        fun setVersion() = apply {
            (response.body.data as? JSResponseBody.JSReadyToWidgetResponseBodyData)?.version = Constants.WIDGET_INTERFACE_VERSION
        }

        fun setType() = apply {
            (response.body.data as? JSResponseBody.JSReadyToWidgetResponseBodyData)?.type = "compose-login"
        }

        fun setUserToken(token: String) = apply {
            (response.body.data as? JSResponseBody.JSGoogleLogInResponseBodyData)?.token = token
        }

        fun setSubRequestHeader(header: JSRequestHeader) = apply {
            (response.body.data as? JSResponseBody.JSGetSdkRequestResponseBodyData)?.header = header
        }

        fun setSubRequestBody(body: JSRequestBody) = apply {
            (response.body.data as? JSResponseBody.JSGetSdkRequestResponseBodyData)?.body = body
        }

        fun setEmail() = apply {
            (response.body.data as? JSResponseBody.JSSetUserEmailResponseBodyData)?.email = null
        }

        fun setLocalData() = apply {
            (response.body.data as? JSResponseBody.JSReadyToWidgetResponseBodyData)?.localDate =
                StorageManager.getAllStorage() as Map<String, Any>
        }

        fun build(): JSResponse = response
    }

    override fun toString(): String {
        return Json.encodeToString(serializer(), this)
    }
}
