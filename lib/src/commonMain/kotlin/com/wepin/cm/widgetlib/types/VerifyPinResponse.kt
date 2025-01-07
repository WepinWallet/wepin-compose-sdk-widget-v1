package com.wepin.cm.widgetlib.types

import kotlinx.serialization.Serializable

@Serializable
data class VerifyPinResponse(
    val status: String,
    val walletId: String,
    val pinVerified: Boolean
)
