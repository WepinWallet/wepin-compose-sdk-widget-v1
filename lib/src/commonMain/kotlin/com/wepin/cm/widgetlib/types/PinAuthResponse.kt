package com.wepin.cm.widgetlib.types

import kotlinx.serialization.Serializable

@Serializable
data class UVD(
    var seqNum: Int? = null,
    var b64SKey: String,
    var b64Data: String
)
