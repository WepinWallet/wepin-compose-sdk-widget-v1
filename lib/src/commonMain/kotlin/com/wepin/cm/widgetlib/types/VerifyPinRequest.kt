package com.wepin.cm.widgetlib.types

import kotlinx.serialization.Serializable

@Serializable
data class VerifyPinRequest(
    val userId: String,
    val walletId: String,
    val UVD: UVD
)
