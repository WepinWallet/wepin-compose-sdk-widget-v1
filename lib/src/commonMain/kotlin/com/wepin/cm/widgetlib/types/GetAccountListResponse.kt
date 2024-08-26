package com.wepin.cm.widgetlib.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetAccountListResponse (
    val walletId: String,
    val accounts: ArrayList<DetailAccount>,
    @SerialName("aa_accounts")
    val aaAccounts: ArrayList<DetailAccount>? = null
)