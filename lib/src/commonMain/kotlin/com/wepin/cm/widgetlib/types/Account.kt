package com.wepin.cm.widgetlib.types

import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val network: String,
    val address: String,
    val contract: String? = null,
//    val isAA: Boolean? = false
)

@Serializable
data class DetailAccount(
    val accountId: String,
    val address: String,
    val eoaAddress: String? = null,
    val addressPath: String,
    val coinId: Int? = null,
    val contract: String? = null,
    val symbol: String,
    val label: String,
    val name: String,
    val network: String,
    val balance: String,
    val decimals: Int,
    val iconUrl: String,
    val ids: String? = null,
    val accountTokenId: String? = null,
    val cmkId: Int? = null,
    val iAA: Boolean? = false
) {
    fun fromAppAccount(): Account {
        return if (this.contract != null && this.accountTokenId != null) {
            Account(
                network = this.network,
                address = this.address,
                contract = this.contract
            )
        } else {
            Account(
                network = this.network,
                address = this.address
            )
        }
    }
}