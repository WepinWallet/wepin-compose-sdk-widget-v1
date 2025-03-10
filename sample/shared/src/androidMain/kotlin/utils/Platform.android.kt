package utils

actual object Platform {
    actual fun isIOS(): Boolean {
        return false
    }

    actual fun isAndroid(): Boolean {
        return true
    }
}