package com.wepin.cm.widgetlib.types

object WepinWidgetError {
    fun getError(
        errorCode: ErrorCode,
        message: String? = null,
    ): String {
        return when (errorCode) {
            ErrorCode.INVALID_APP_KEY -> {
                "Invalid app key"
            }
            ErrorCode.INVALID_PARAMETER -> {
                "Invalid parameter"
            }
            ErrorCode.INVALID_TOKEN -> {
                "Token does not exist"
            }
            ErrorCode.INVALID_LOGIN_PROVIDER -> {
                "Invalid login provider"
            }
            ErrorCode.NOT_INITIALIZED_ERROR -> {
                "Not initialized error"
            }
            ErrorCode.ALREADY_INITIALIZED_ERROR -> {
                "Already Initialized"
            }
            ErrorCode.NOT_ACTIVITY -> {
                "Context is not activity"
            }
            ErrorCode.USER_CANCELLED -> {
                "User Cancelled"
            }
            ErrorCode.UNKNOWN_ERROR -> {
                "unknown error: $message"
            }
            ErrorCode.NOT_CONNECTED_INTERNET -> {
                "No internet connection"
            }
            ErrorCode.FAILED_LOGIN -> {
                "Failed Oauth log in"
            }
            ErrorCode.ALREADY_LOGOUT -> {
                "already logout"
            }
            ErrorCode.INVALID_EMAIL_DOMAIN -> {
                "Invalid email domain"
            }
            ErrorCode.FAILED_SEND_EMAIL -> {
                "Failed to send email"
            }
            ErrorCode.REQUIRED_EMAIL_VERIFIED -> {
                "Email verification required"
            }
            ErrorCode.INCORRECT_EMAIL_FORM -> {
                "Incorrect email format"
            }
            ErrorCode.INCORRECT_PASSWORD_FORM -> {
                "Incorrect password format"
            }
            ErrorCode.NOT_INITIALIZED_NETWORK -> {
                "Network Manager not initialized."
            }
            ErrorCode.REQUIRED_SIGNUP_EMAIL -> {
                "Email sign-up required"
            }
            ErrorCode.FAILED_EMAIL_VERIFIED -> {
                "Failed to verify email."
            }
            ErrorCode.FAILED_PASSWORD_SETTING -> {
                "Failed to set password"
            }
            ErrorCode.EXISTED_EMAIL -> {
                "Email already exists"
            }
            ErrorCode.INVALID_LOGIN_SESSION -> {
                "Invalid Login Session"
            }
            ErrorCode.INCORRECT_LIFECYCLE_EXCEPTION -> {
                "Incorrect Lifecycle Exception"
            }
            ErrorCode.ACCOUNT_NOT_FOUND -> {
                "Account Not Found"
            }
            ErrorCode.FAILED_REGISTER -> {
                "Failed Register"
            }
            ErrorCode.FAILED_SEND -> {
                "Failed Send"
            }
            ErrorCode.NFT_NOT_FOUND -> {
                "NFT not Found"
            }
        }
    }
}

enum class ErrorCode {
    INVALID_APP_KEY,
    INVALID_PARAMETER,
    INVALID_LOGIN_PROVIDER,
    INVALID_TOKEN,
    INVALID_LOGIN_SESSION,
    NOT_INITIALIZED_ERROR,
    ALREADY_INITIALIZED_ERROR,
    NOT_ACTIVITY,
    USER_CANCELLED,
    UNKNOWN_ERROR,
    NOT_CONNECTED_INTERNET,
    FAILED_LOGIN,
    ALREADY_LOGOUT,
    INVALID_EMAIL_DOMAIN,
    FAILED_SEND_EMAIL,
    REQUIRED_EMAIL_VERIFIED,
    INCORRECT_EMAIL_FORM,
    INCORRECT_PASSWORD_FORM,
    NOT_INITIALIZED_NETWORK,
    REQUIRED_SIGNUP_EMAIL,
    FAILED_EMAIL_VERIFIED,
    FAILED_PASSWORD_SETTING,
    EXISTED_EMAIL,
    FAILED_REGISTER,
    FAILED_SEND,
    INCORRECT_LIFECYCLE_EXCEPTION,
    ACCOUNT_NOT_FOUND,
    NFT_NOT_FOUND
}
