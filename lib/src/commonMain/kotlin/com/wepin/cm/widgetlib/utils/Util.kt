package com.wepin.cm.widgetlib.utils

import com.wepin.cm.widgetlib.BuildConfig
import kotlinx.serialization.json.*

fun getVersionMetaDataValue(): String {
    return BuildConfig.PROJECT_VERSION
}

expect fun getDomain(): String

expect fun getPlatform(): Int

fun convertJsonElementToAny(element: JsonElement): Any {
    return when (element) {
        is JsonPrimitive -> {
            when {
                element.isString -> element.content
                element.booleanOrNull != null -> element.boolean
                element.intOrNull != null -> element.int
                element.doubleOrNull != null -> element.double
                else -> element.content
            }
        }
        // 필요시 다른 JsonElement 타입 (JsonObject, JsonArray 등)도 처리 가능
        else -> element.toString() // 기본적으로는 문자열로 변환
    }
}