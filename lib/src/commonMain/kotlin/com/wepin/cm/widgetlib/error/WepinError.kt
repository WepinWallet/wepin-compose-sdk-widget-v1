package com.wepin.cm.widgetlib.error

import com.wepin.cm.widgetlib.types.ErrorCode
import com.wepin.cm.widgetlib.types.WepinWidgetError
import kotlinx.serialization.json.Json

class WepinError : Exception {
    var errorReason: Json? = null
    var errorMessage: String? = null
    var code: Int = 0

    constructor() {
        errorReason = null
    }

    constructor(response: Json?) {
        errorReason = response
    }

    constructor(exceptionMessage: String?) : super(exceptionMessage) {
        errorMessage = null
    }

    constructor(exceptionMessage: String?, reason: Throwable?) : super(exceptionMessage, reason) {
        errorReason = null
    }

    constructor(cause: Throwable?) : super(cause) {
        errorReason = null
    }

    constructor(
        code: Int,
        errorDescription: String,
    ) : super(errorDescription) {
        this.code = code
        this.errorMessage = errorDescription
    }

    // package
    fun setErrorCode(errCode: Int) {
        this.code = errCode
    }

    fun getErrorCode(): Int {
        return code
    }

    companion object {
        val INVALID_PARAMETER = WepinError.generalEx(ErrorCode.INVALID_PARAMETER)
        val NOT_INITIALIZED_ERROR = WepinError.generalEx(ErrorCode.NOT_INITIALIZED_ERROR)
        val ALREADY_INITIALIZED_ERROR = WepinError.generalEx(ErrorCode.ALREADY_INITIALIZED_ERROR)
        val NOT_ACTIVITY = WepinError.generalEx(ErrorCode.NOT_ACTIVITY)
        val NOT_CONNECTED_INTERNET = WepinError.generalEx(ErrorCode.NOT_CONNECTED_INTERNET)
        val UNKNOWN_ERROR = WepinError.generalEx(ErrorCode.UNKNOWN_ERROR)
        val INCORRECT_LIFECYCLE_EXCEPTION = WepinError.generalEx(ErrorCode.INCORRECT_LIFECYCLE_EXCEPTION)
        val ACCOUNT_NOT_FOUND = WepinError.generalEx(ErrorCode.ACCOUNT_NOT_FOUND)
        val FAILED_REGISTER = WepinError.generalEx(ErrorCode.FAILED_REGISTER)
        val FAILED_SEND = WepinError.generalEx(ErrorCode.FAILED_SEND)
        val NFT_NOT_FOUND = WepinError.generalEx(ErrorCode.NFT_NOT_FOUND)

        private fun generalEx(errorDescription: ErrorCode): WepinError {
            return WepinError(WepinWidgetError.getError(errorDescription))
        }

        fun generalUnKnownEx(message: String?): WepinError {
            return WepinError("${WepinWidgetError.getError(ErrorCode.UNKNOWN_ERROR)} - $message")
        }

        fun generateExWithMessage(errorCode: ErrorCode, message: String?): WepinError {
            return WepinError("${WepinWidgetError.getError(errorCode)} + $message")
        }
    }
}
