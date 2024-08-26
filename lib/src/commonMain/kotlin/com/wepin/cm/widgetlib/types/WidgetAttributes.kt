package com.wepin.cm.widgetlib.types

import kotlinx.serialization.Serializable

@Serializable
data class WidgetAttributes(
    val type: String = "",
    val defaultLanguage: String = "",
    val defaultCurrency: String = ""
)