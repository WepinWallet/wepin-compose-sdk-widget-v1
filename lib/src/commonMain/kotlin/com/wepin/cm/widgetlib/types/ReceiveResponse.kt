package com.wepin.cm.widgetlib.types

import kotlinx.serialization.Serializable

@Serializable
data class ReceiveResponse(
    val account: Account
)
