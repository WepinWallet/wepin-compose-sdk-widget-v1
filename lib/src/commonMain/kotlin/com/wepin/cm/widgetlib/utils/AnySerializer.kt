package com.wepin.cm.widgetlib.utils

import com.wepin.cm.widgetlib.types.JSRegisterRequestParameter
import com.wepin.cm.loginlib.types.WepinLoginStatus
import com.wepin.cm.widgetlib.types.Account
import com.wepin.cm.widgetlib.types.JSSendRequestParameter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

object AnySerializer: KSerializer<Any> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Any") {
        element<JsonElement>("value")
    }

    override fun serialize(encoder: Encoder, value: Any) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw IllegalStateException("This serializer can be used only with JSON format")
        val jsonElement = when (value) {
            is Int -> JsonPrimitive(value)
            is String -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is Double -> JsonPrimitive(value)
            is Float -> JsonPrimitive(value)
            is Long -> JsonPrimitive(value)
            is Short -> JsonPrimitive(value)
            is Byte -> JsonPrimitive(value)
            is JsonElement -> value
            is JSRegisterRequestParameter ->  buildJsonObject {
                put("loginStatus", JsonPrimitive(value.loginStatus.value))
                put("pinRequired", JsonPrimitive(value.pinRequired))
            }
            is JSSendRequestParameter -> buildJsonObject {
                put("account", Json.encodeToJsonElement(Account.serializer(), value.account))
                put("from", JsonPrimitive(value.from))
                put("to", JsonPrimitive(value.to))
                put("value", JsonPrimitive(value.value))
            }
            is Map<*, *> -> JsonObject(value.mapKeys { it.key.toString() }.mapValues { serializeValue(it.value) })
            else -> throw IllegalStateException("Unsupported type: ${value::class}")
        }
        jsonEncoder.encodeJsonElement(jsonElement)
    }

    override fun deserialize(decoder: Decoder): Any {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalStateException("This serializer can be used only with JSON format")
        return deserializeValue(jsonDecoder.decodeJsonElement())
    }

    private fun serializeValue(value: Any?): JsonElement {
        return when (value) {
            null -> JsonPrimitive(null as String?)
            is Int -> JsonPrimitive(value)
            is String -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is Double -> JsonPrimitive(value)
            is Float -> JsonPrimitive(value)
            is Long -> JsonPrimitive(value)
            is Short -> JsonPrimitive(value)
            is Byte -> JsonPrimitive(value)
            is JSRegisterRequestParameter -> buildJsonObject {
                put("loginStatus", JsonPrimitive(value.loginStatus.value))
                put("pinRequired", JsonPrimitive(value.pinRequired))
            }
            is JSSendRequestParameter -> buildJsonObject {
                put("account", Json.encodeToJsonElement(Account.serializer(), value.account))
                put("from", JsonPrimitive(value.from))
                put("to", JsonPrimitive(value.to))
                put("value", JsonPrimitive(value.value))
            }
            is Map<*, *> -> JsonObject(value.mapKeys { it.key.toString() }.mapValues { serializeValue(it.value) })
            else -> throw IllegalStateException("Unsupported type: ${value::class}")
        }
    }

    private fun deserializeValue(element: JsonElement): Any {
        return when (element) {
            is JsonPrimitive -> when {
                element.isString -> element.content
                element.booleanOrNull != null -> element.boolean
                element.intOrNull != null -> element.int
                element.longOrNull != null -> element.long
                element.doubleOrNull != null -> element.double
                else -> throw IllegalStateException("Unsupported primitive type: ${element.content}")
            }
            is JsonObject -> {
                if (element.containsKey("loginStatus") && element.containsKey("pinRequired")) {
                    JSRegisterRequestParameter(
                        loginStatus = WepinLoginStatus.fromValue(element["loginStatus"]?.jsonPrimitive?.content ?: "")
                            ?: throw IllegalArgumentException("Unknown WepinLoginStatus"),
                        pinRequired = element["pinRequired"]?.jsonPrimitive?.boolean ?: false
                    )
                } else if (element.containsKey("account") && element.containsKey("from") && element.containsKey("to") && element.containsKey("value")) {
                    JSSendRequestParameter(
                        account = Json.decodeFromJsonElement(Account.serializer(), element["account"]!!),
                        from = element["from"]?.jsonPrimitive?.content ?: "",
                        to = element["to"]?.jsonPrimitive?.content ?: "",
                        value = element["value"]?.jsonPrimitive?.content ?: ""
                    )
                } else {
                    element.mapValues { deserializeValue(it.value) }
                }
            }
            is JsonArray -> element.map { deserializeValue(it) }
            else -> throw IllegalStateException("Unsupported JsonElement type: ${element::class}")
        }
    }

}