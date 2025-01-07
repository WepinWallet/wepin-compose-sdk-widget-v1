package com.wepin.cm.widgetlib.types

import kotlinx.serialization.Serializable

@Serializable
data class LoginProvider(
    val provider: String,
    val clientId: String
)

@Serializable
data class LoginWithUIParameter(
    val loginProviders: Array<LoginProvider>,
    val email: String? = null
)
