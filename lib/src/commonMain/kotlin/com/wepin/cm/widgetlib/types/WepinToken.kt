package com.wepin.cm.widgetlib.types

import kotlinx.serialization.Serializable

@Serializable
data class WepinToken (
    val accessToken: String,
    val refreshToken: String,
)