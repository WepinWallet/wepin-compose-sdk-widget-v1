package com.wepin.cm.widgetlib.utils

import com.wepin.cm.loginlib.types.WepinLoginStatus
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object WepinLoginStatusSerializer : KSerializer<WepinLoginStatus> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("WepinLoginStatus", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: WepinLoginStatus) {
        encoder.encodeString(value.value)
    }

    override fun deserialize(decoder: Decoder): WepinLoginStatus {
        val value = decoder.decodeString()
        return WepinLoginStatus.fromValue(value)
            ?: throw IllegalArgumentException("Unknown WepinLoginStatus value: $value")
    }
}
