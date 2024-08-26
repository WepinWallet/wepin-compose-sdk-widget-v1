package com.wepin.cm.widgetlib.types

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val appId: String,
    val userId: String,
    val loginStatus: String,
    val walletId: String
)
