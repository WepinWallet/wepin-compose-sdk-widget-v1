package com.wepin.cm.widgetlib.types

import kotlinx.serialization.Serializable

@Serializable
data class AccountDetailResponse(
    val account: Account
)
