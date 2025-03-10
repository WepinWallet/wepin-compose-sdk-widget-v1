package com.wepin.cm.widgetlib.utils

import com.wepin.cm.loginlib.types.StorageDataType
import com.wepin.cm.widgetlib.types.JSRegisterRequestParameter
import com.wepin.cm.loginlib.types.WepinLoginStatus
import com.wepin.cm.widgetlib.types.Account
import com.wepin.cm.widgetlib.types.JSGetLoginInfoRequestParameter
import com.wepin.cm.widgetlib.types.JSPinAuthRequestParameter
import com.wepin.cm.widgetlib.types.JSReceiveRequestParameter
import com.wepin.cm.widgetlib.types.JSSendRequestParameter
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
            is JSReceiveRequestParameter -> buildJsonObject {
                put("account", Json.encodeToJsonElement(Account.serializer(), value.account))
            }
            is JSPinAuthRequestParameter -> buildJsonObject {
                put("count", JsonPrimitive(value.count))
            }
            is JSGetLoginInfoRequestParameter -> buildJsonObject {
                put("provider", JsonPrimitive(value.provider))
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
            is JSReceiveRequestParameter -> buildJsonObject {
                put("account", Json.encodeToJsonElement(Account.serializer(), value.account))
            }
            is JSPinAuthRequestParameter -> buildJsonObject {
                put("count", JsonPrimitive(value.count))
            }
            is JSGetLoginInfoRequestParameter -> buildJsonObject {
                put("provider", JsonPrimitive(value.provider))
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
                } else if (element.containsKey("account")) {
                    JSReceiveRequestParameter(
                        account = Json.decodeFromJsonElement(Account.serializer(), element["account"]!!)
                    )
                } else if (element.containsKey("count")) {
                    JSPinAuthRequestParameter(count = element["count"]?.jsonPrimitive?.int ?: 1)
                } else if (element.containsKey("provider") && element.size == 1) {
                    JSGetLoginInfoRequestParameter(provider = element["provider"]?.jsonPrimitive?.content ?: "")
                } else {
                    if (element.containsKey("data")) {
                        val value = element.get("data")
                        if (value !== null) {
                            deserializeValue(value)
                        } else {
                            element
                        }
                    }
                    else {
                        element.mapNotNull { (key, value) ->
                            val json = Json {
                                prettyPrint = true
                                isLenient = true
                                encodeDefaults = true
                            }
                            when (key) {
                                "firebase:wepin" -> {
                                    val data = json.decodeFromJsonElement<StorageDataType.FirebaseWepin>(value)
                                    key to data
                                }
                                "wepin:connectUser" -> {
                                    val data = json.decodeFromJsonElement<StorageDataType.WepinToken>(value)
                                    key to data
                                }
                                "user_id" -> key to value.toString().replace("\"", "")
                                "user_status" -> {
                                    val data = json.decodeFromJsonElement<StorageDataType.UserStatus>(value)
                                    key to data
                                }
                                "wallet_id" -> key to value.toString().replace("\"", "")
                                "user_info" -> {
                                    val data = json.decodeFromJsonElement<StorageDataType.UserInfo>(value)
                                    key to data
                                }
                                "oauth_provider_pending" -> key to value.toString().replace("\"", "")
                                else -> null
                            }
                        }.toMap()
                    }
                }
            }
            is JsonArray -> element.map { deserializeValue(it) }
            else -> throw IllegalStateException("Unsupported JsonElement type: ${element::class}")
        }
    }

}