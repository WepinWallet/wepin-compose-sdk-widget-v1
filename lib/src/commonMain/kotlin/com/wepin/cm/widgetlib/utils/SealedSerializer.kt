package com.wepin.cm.widgetlib.utils

import com.wepin.cm.widgetlib.types.JSResponse
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

object SealedSerializer: JsonContentPolymorphicSerializer<JSResponse.JSResponseBody.JSResponseBodyData>(
    JSResponse.JSResponseBody.JSResponseBodyData::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<JSResponse.JSResponseBody.JSResponseBodyData> {
        try {
            val jsonObject = element.jsonObject
            return when {
                jsonObject.containsKey("appKey") -> JSResponse.JSResponseBody.JSReadyToWidgetResponseBodyData.serializer()
                jsonObject.containsKey("token") -> JSResponse.JSResponseBody.JSGoogleLogInResponseBodyData.serializer()
                jsonObject.containsKey("header") -> JSResponse.JSResponseBody.JSGetSdkRequestResponseBodyData.serializer()
                jsonObject.containsKey("email") -> JSResponse.JSResponseBody.JSSetUserEmailResponseBodyData.serializer()
                jsonObject.containsKey("walletId") -> JSResponse.JSResponseBody.JSRegisterResponseBodyData.serializer()
                else -> {
                    throw IllegalArgumentException("Unsupported JSResponseBodyData type.")
                }
            }
        } catch (e: Exception) {
            if (element.toString().startsWith("\"")) {
                // JsonElement를 String으로 변환한 후 따옴표 제거
                val stringData = element.toString().trim('"')
                // 커스텀 KSerializer 생성 및 반환
                return object : KSerializer<JSResponse.JSResponseBody.JSResponseBodyData> {
                    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("JSStringResponse", PrimitiveKind.STRING)

                    override fun deserialize(decoder: Decoder): JSResponse.JSResponseBody.JSResponseBodyData {
                        return JSResponse.JSResponseBody.JSStringResponse(stringData)
                    }

                    override fun serialize(encoder: Encoder, value: JSResponse.JSResponseBody.JSResponseBodyData) {
                        // 직렬화는 이 예제에서 필요 없으므로 구현하지 않음
                    }
                }
            } else {
                throw e // 다른 예외는 그대로 다시 던짐
            }
        }
    }
}