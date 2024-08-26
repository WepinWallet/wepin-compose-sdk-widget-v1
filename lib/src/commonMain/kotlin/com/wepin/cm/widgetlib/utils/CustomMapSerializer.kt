package com.wepin.cm.widgetlib.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull

object CustomMapSerializer : KSerializer<Map<String, Any>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Map<String, Any>") {
        element<String>("key")
        element<String>("value")
    }

    override fun serialize(encoder: Encoder, value: Map<String, Any>) {
        val mapSerializer = MapSerializer(String.serializer(), JsonPrimitive.serializer())

        val serializedMap = value.mapValues { (_, v) ->
            when (v) {
                is String -> JsonPrimitive(v)
                is Int -> JsonPrimitive(v)
                is Boolean -> JsonPrimitive(v)
                is Double -> JsonPrimitive(v)
                // 필요한 다른 타입들도 여기에 추가할 수 있습니다.
                else -> throw IllegalArgumentException("Unsupported type: ${v::class}")
            }
        }

        encoder.encodeSerializableValue(mapSerializer, serializedMap)
    }

    override fun deserialize(decoder: Decoder): Map<String, Any> {
        val mapSerializer = MapSerializer(String.serializer(), JsonPrimitive.serializer())
        val deserializedMap = decoder.decodeSerializableValue(mapSerializer)

        return deserializedMap.mapValues { (_, v) ->
            when {
                v.isString -> v.content
                v.intOrNull != null -> v.int
                v.booleanOrNull != null -> v.boolean
                v.doubleOrNull != null -> v.double
                // 필요한 다른 타입들도 여기에 추가할 수 있습니다.
                else -> throw IllegalArgumentException("Unsupported type: $v")
            }
        }
    }
}
