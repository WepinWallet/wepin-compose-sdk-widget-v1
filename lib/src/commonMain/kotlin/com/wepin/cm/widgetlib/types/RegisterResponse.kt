package com.wepin.cm.widgetlib.types

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val success: Boolean,
    val walletId: String,
)
