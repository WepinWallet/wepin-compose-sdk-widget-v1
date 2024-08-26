package com.wepin.cm.widgetlib.types

import kotlinx.serialization.Serializable

@Serializable
data class GetAccountBalanceResponse(
    val decimals: Int,
    val symbol: String,
    val tokens: ArrayList<TokenResponse>,
    val balance: String
)

@Serializable
data class TokenResponse(
    val contract: String,
    val name: String? = null,
    val decimals: Int,
    val symbol: String,
    val tokenId: Int,
    val balance: String
)