package utils

actual object Platform {
    actual fun isIOS(): Boolean {
        return true
    }

    actual fun isAndroid(): Boolean {
        return false
    }
}