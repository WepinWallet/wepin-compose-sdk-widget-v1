package com.wepin.cm.widgetlib.types

enum class WepinProviderCommand(val value: String) {
    REQUEST_ENABLE("request_enable"),
    SIGN_TRANSACTION("sign_transaction"),
    SEND_TRANSACTION("send_transaction"),
    SIGN_TYPED_DATA("sign_typed_data"),
    SIGN("sign"),
    WALLET_SWITCH_ETHEREUM_CHAIN("wallet_switchEthereumChain")
    ;
}

enum class WepinAdminCommand(val value: String) {
    SIGNup_EMAIL("signup_email"),
    LOGIN_EMAIL("login_email"),
    REGISTER_WEPIN("register_wepin"),
    GET_BALANCE("get_balance"),
    GET_SDK_REQUEST("get_sdk_request"),
    SEND_TRANSACTION_WITHOUT_PROVIDER("send_transaction_without_provider")
    ;
}
//enum class WepinCommand (val value: String) {
//    //WepinProviderCommand
//    REQUEST_ENABLE("request_enable"),
//    SIGN_TRANSACTION("sign_transaction"),
//    SEND_TRANSACTION("send_transaction"),
//    SIGN_TYPED_DATA("sign_typed_data"),
//    SIGN("sign"),
//    WALLET_SWITCH_ETHEREUM_CHAIN("wallet_switchEthereumChain"),
//    //WepinAdminCommand
//    SIGNup_EMAIL("signup_email"),
//    LOGIN_EMAIL("login_email"),
//    REGISTER_WEPIN("register_wepin"),
//    GET_BALANCE("get_balance"),
//    GET_SDK_REQUEST("get_sdk_request"),
//    SEND_TRANSACTION_WITHOUT_PROVIDER("send_transaction_without_provider"),
//    //
//    READY_TO_WIDGET("ready_to_widget"),
//    INITIALIZED_WIDGET("initialized_widget"),
//    SET_ACCOUNTS("set_accounts"),
//    CLOSE_WEPIN_WIDGET("close_wepin_widget"),
//    PROVIDER_REQUEST("provider_request"),
//    DEQUEUE_REQUEST("dequeue_request"),
//    SET_TOKEN("set_token"),
//    SET_USER_INFO("set_user_info"),
//    WEPIN_LOGOUT("wepin_logout"),
//    SET_USER_EMAIL("set_user_email"),
//    SET_LOCAL_STORAGE("set_local_storage"),
//    GET_CLIPBOARD("get_clipboard")
//    ;
//    companion object {
//        fun fromValue(value: String): WepinCommand? {
//            return WepinCommand.entries.find { it.value == value}
//        }
//    }
//}

interface Command {
    companion object {
        /**
         * Commands for JS processor
         */
        const val CMD_READY_TO_WIDGET = "ready_to_widget"
        const val CMD_INITIALIZED_WIDGET = "initialized_widget"
        const val CMD_CLOSE_WEPIN_WIDGET = "close_wepin_widget"
        const val CMD_GET_SDK_REQUEST = "get_sdk_request"
        const val CMD_GET_CLIPBOARD = "get_clipboard"
        const val CMD_SET_LOCAL_STORAGE = "set_local_storage"
        const val CMD_SET_USER_EMAIL = "set_user_email"
        const val CMD_WEPIN_REGISTER = "register_wepin"
        const val CMD_SEND_TRANSACTION_WITHOUT_PROVIDER = "send_transaction_without_provider"
    }
}