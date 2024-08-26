package com.wepin.cm.widgetlib.storage

import com.wepin.cm.loginlib.error.WepinError
import com.wepin.cm.loginlib.types.KeyType
import com.wepin.cm.widgetlib.const.WidgetUrl
import com.wepin.cm.widgetlib.types.WidgetAttributes

object AppData {
    private var _appId: String = ""
    private var _appKey: String = ""
    private var _context: Any? = null
    private var _attributes: WidgetAttributes? = null
    private var _widgetUrl: String = ""

    fun setAppId(value: String) {
        _appId = value
    }

    fun setAppKey(value: String) {
        _appKey = value
    }

    fun setContext(value: Any) {
        _context = value
    }

    fun setAttributes(value: WidgetAttributes) {
        _attributes = value
    }
    fun setWidgetUrl(apiKey: String) {
        var keytype: String
        when (KeyType.fromAppKey(apiKey)) {
            KeyType.DEV -> {
                keytype = "dev"
            }

            KeyType.STAGE -> {
                keytype = "stage"
            }

            KeyType.PROD -> {
                keytype = "prod"
            }

            else -> {
                throw WepinError.INVALID_APP_KEY
            }
        }
        _widgetUrl = WidgetUrl.getWidgetUrl(keytype)
    }

    fun getAppId(): String {
        return _appId
    }

    fun getAppKey(): String {
        return _appKey
    }

    fun getContext(): Any? {
        return _context
    }

    fun getAttributes(): WidgetAttributes? {
        return _attributes
    }

    fun getWidgetURL(): String {
        return _widgetUrl
    }
}