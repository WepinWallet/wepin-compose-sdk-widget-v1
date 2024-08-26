package com.wepin.cm.widgetlib.types

import kotlinx.serialization.Serializable

@Serializable
data class TokenBalanceInfo(
    val symbol: String,
    val balance: String,
    val contract: String
)

@Serializable
data class AccountBalanceInfo(
    val address: String,
    val network: String,
    val symbol: String,
    val balance: String,
    val tokens: ArrayList<TokenBalanceInfo>
)
