package com.wepin.cm.widgetlib.types

import kotlinx.serialization.Serializable

@Serializable
data class TxData(
    val toAddress: String,
    var amount: String
)

@Serializable
data class SendData(
    val account: Account,
    val txData: TxData
)
