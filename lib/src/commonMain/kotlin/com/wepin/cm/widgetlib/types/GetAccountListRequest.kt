package com.wepin.cm.widgetlib.types

data class GetAccountListRequest (
    val walletId: String,
    val userId: String,
    val localeId: String
)