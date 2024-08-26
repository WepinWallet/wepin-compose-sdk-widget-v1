package com.wepin.cm.widgetlib.types

enum class WepinLifeCycle(val value: String) {
    NOT_INITIALIZED("not_initialized"),
    INITIALIZING("initializing"),
    INITIALIZED("initialized"),
    BEFORE_LOGIN("before_login"),
    LOGIN("login"),
    LOGIN_BEFORE_REGISTER("login_before_register"),
    ;

    companion object {
        fun fromValue(value: String): WepinLifeCycle? {
            return WepinLifeCycle.entries.find { it.value == value}
        }
    }
}