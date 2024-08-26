package com.wepin.cm.widgetlib.types

import kotlinx.serialization.Serializable

@Serializable
data class GetNFTRequest(
    val walletId: String,
    val userId: String
)