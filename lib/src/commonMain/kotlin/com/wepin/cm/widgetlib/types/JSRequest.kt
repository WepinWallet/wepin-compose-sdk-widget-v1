package com.wepin.cm.widgetlib.types

import com.wepin.cm.loginlib.types.WepinLoginStatus
import com.wepin.cm.widgetlib.utils.AnySerializer
import kotlinx.serialization.Serializable

@Serializable
data class JSRequestHeader(
    val id: Long,
    val request_to: String,
    val request_from: String
)

@Serializable
data class JSRequestBody(
    val command: String,
    @Serializable(with = AnySerializer::class) val parameter: Any? = null,
)

@Serializable
data class JSRequest(val header: JSRequestHeader, val body: JSRequestBody)

@Serializable
data class JSRegisterRequestParameter(
    val loginStatus: WepinLoginStatus,
    val pinRequired: Boolean
)

@Serializable
data class JSSendRequestParameter(
    val account: Account,
    val from: String,
    val to: String,
    val value: String
)