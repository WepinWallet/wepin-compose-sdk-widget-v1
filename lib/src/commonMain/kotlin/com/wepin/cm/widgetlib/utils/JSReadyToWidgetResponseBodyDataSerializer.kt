import com.wepin.cm.loginlib.types.StorageDataType
import com.wepin.cm.widgetlib.types.WidgetAttributes
import com.wepin.cm.widgetlib.types.JSResponse
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

object JSReadyToWidgetResponseBodyDataSerializer : KSerializer<JSResponse.JSResponseBody.JSReadyToWidgetResponseBodyData> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("JSReadyToWidgetResponseBodyData") {
        element<String>("appKey")
        element<String>("domain")
        element<Int>("platform")
        element<WidgetAttributes>("attributes")
        element<String>("type")
        element<JsonObject>("localDate")
        element<Int>("version")
    }

    override fun serialize(encoder: Encoder, value: JSResponse.JSResponseBody.JSReadyToWidgetResponseBodyData) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.appKey)
            encodeStringElement(descriptor, 1, value.domain)
            encodeIntElement(descriptor, 2, value.platform)
            encodeSerializableElement(descriptor, 3, WidgetAttributes.serializer(), value.attributes)
            encodeStringElement(descriptor, 4, value.type)

            val localDateJson = value.localDate.mapValues { (key, value) ->
                when (key) {
                    "wepin:connectUser" -> {
                        // WepinToken 직렬화
                        Json.encodeToJsonElement(StorageDataType.WepinToken.serializer(), value as StorageDataType.WepinToken)
                    }
                    "firebase:wepin" -> {
                        // FirebaseWepin 직렬화
                        Json.encodeToJsonElement(StorageDataType.FirebaseWepin.serializer(), value as StorageDataType.FirebaseWepin)
                    }
                    "user_status" -> {
                        // UserStatus 직렬화
                        Json.encodeToJsonElement(StorageDataType.UserStatus.serializer(), value as StorageDataType.UserStatus)
                    }
                    "user_info" -> {
                        // 다른 객체로 직렬화
                        Json.encodeToJsonElement(StorageDataType.UserInfo.serializer(), value as StorageDataType.UserInfo)
                    }
                    "app_language" -> {
                        Json.encodeToJsonElement(StorageDataType.AppLanguage.serializer(), value as StorageDataType.AppLanguage)
                    }
                    else -> JsonPrimitive(value.toString())
                }
            }

            encodeSerializableElement(descriptor, 5, JsonObject.serializer(), JsonObject(localDateJson))
            encodeIntElement(descriptor, 6, value.version)
        }
    }

    override fun deserialize(decoder: Decoder): JSResponse.JSResponseBody.JSReadyToWidgetResponseBodyData {
        return decoder.decodeStructure(descriptor) {
            var appKey = ""
            var domain = ""
            var platform = 0
            var attributes = WidgetAttributes()
            var type = ""
            var localDate = mapOf<String, Any>()
            var version = 0

            loop@ while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> appKey = decodeStringElement(descriptor, 0)
                    1 -> domain = decodeStringElement(descriptor, 1)
                    2 -> platform = decodeIntElement(descriptor, 2)
                    3 -> attributes = decodeSerializableElement(descriptor, 3, WidgetAttributes.serializer())
                    4 -> type = decodeStringElement(descriptor, 4)
                    5 -> localDate = decodeSerializableElement(descriptor, 5, JsonObject.serializer()).map { it.key to it.value.toString() }.toMap()
                    6 -> version = decodeIntElement(descriptor, 6)
                    else -> break@loop
                }
            }

            JSResponse.JSResponseBody.JSReadyToWidgetResponseBodyData(
                appKey = appKey,
                domain = domain,
                platform = platform,
                attributes = attributes,
                type = type,
                localDate = localDate,
                version = version
            )
        }
    }
}
