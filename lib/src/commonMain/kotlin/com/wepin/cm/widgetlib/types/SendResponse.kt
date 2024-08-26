package com.wepin.cm.widgetlib.types

import kotlinx.serialization.Serializable

@Serializable
data class SendResponse(
    val txId: String
)
