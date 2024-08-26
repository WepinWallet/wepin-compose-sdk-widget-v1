package com.wepin.cm.widgetlib.info

import com.wepin.cm.widgetlib.error.WepinError

object LocaleManager {

    fun getNumberFromLocale(locale: String): Int {
        return when (locale) {
            "ko" -> 1
            "en" -> 2
            "ja" -> 3
            else -> throw WepinError(WepinError.INCORRECT_LIFECYCLE_EXCEPTION)
        }
    }

    fun getLocaleFromNumber(number: Int): String {
        return when (number) {
            1 -> "ko"
            2 -> "en"
            3 -> "ja"
            else -> throw WepinError(WepinError.UNKNOWN_ERROR)
        }
    }
}