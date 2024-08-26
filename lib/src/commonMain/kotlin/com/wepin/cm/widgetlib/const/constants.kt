package com.wepin.cm.widgetlib.const

internal class WidgetUrl {
    companion object {
        fun getWidgetUrl(env: String): String {
            return when (env) {
                "local" -> "https://localhost:8989"
                "dev" -> "https://dev-v1-widget.wepin.io"
                "stage" -> "https://stage-v1-widget.wepin.io"
                "prod" -> "https://v1-widget.wepin.io"
                else -> ""
            }
        }

        fun getSdkUrl(env: String): String {
            return when (env) {
                "dev" -> "https://dev-v1-sdk.wepin.io/"
                "stage" -> "https://stage-v1-sdk.wepin.io/"
                "prod" -> "https://v1-sdk.wepin.io/"
                else -> ""
            }
        }
    }
}

object Constants {
    const val WIDGET_INTERFACE_VERSION = 0

    const val TITLE_APP_SETTING = "titleAppSetting"
    const val KEY_APP_INSTANCE_ID = "keyInstanceId"

    const val KEY_WIDGET_SERVER_URL = "keyWidgetServerUrl"
    const val KEY_SDK_SERVER_URL = "keySdkServerUrl"
    const val WEBVIEW_TYPE_WIDGET = "webServerTypeMain"
    const val KEY_GOOGLE_AUTH_TOKEN = "keyGoogleAuthToken"
    const val KEY_CURRENT_LOGIN_TYPE = "keyCurrentLogInType"
    const val LOGIN_TYPE_GOOGLE = "google"
    const val WIDGET_VIEW_TYPE_HIDE = "hide" // default
    const val WIDGET_VIEW_TYPE_SHOW = "show"
    const val KEY_RETURN_FROM_APP_MAIN = "keyReturnFromAppMain"
    const val LANGUAGE_KO = "ko"
    const val LANGUAGE_EN = "en" // default
    const val PREFIX_DEV_APP_KEY = "ak_dev_"
    const val PREFIX_STAGE_APP_KEY = "ak_test_"
    const val PREFIX_LIVE_APP_KEY = "ak_live_"

    const val LOGIN_METHOD_EMAIL = "EMAIL"
    const val LOGIN_METHOD_ACCESS_TOKEN = "ACCESS_TOKEN"
    const val LOGIN_METHOD_ID_TOKEN = "ID_TOKEN"
    const val LOGIN_METHOD_OAUTH_PROVIDER = "OAUTH"
}