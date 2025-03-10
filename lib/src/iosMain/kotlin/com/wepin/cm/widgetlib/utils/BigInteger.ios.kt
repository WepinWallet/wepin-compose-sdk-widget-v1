package com.wepin.cm.widgetlib.utils

import platform.Foundation.NSDecimalNumber

actual class BigInteger actual constructor(value: String) {
    private val delegate = NSDecimalNumber(string = value)

    actual operator fun div(other: BigInteger): BigInteger {
        val result = delegate.decimalNumberByDividingBy(other.delegate)
        // 정수 부분만 추출
        val wholePart = result.doubleValue.toLong()
        return BigInteger(wholePart.toString())
    }

    actual operator fun rem(other: BigInteger): BigInteger {
        // Remainder 계산은 직접 수행해야 합니다.
        val divisionResult = delegate.decimalNumberByDividingBy(other.delegate)
        val integerPart = divisionResult.doubleValue.toLong()
        val remainder = delegate.decimalNumberBySubtracting(
            other.delegate.decimalNumberByMultiplyingBy(NSDecimalNumber(longLong = integerPart))
        )
        return BigInteger(remainder.stringValue)
    }

    actual fun pow(exponent: Int): BigInteger {
        val result = delegate.decimalNumberByRaisingToPower(exponent.toULong())
        return BigInteger(result.stringValue)
    }

    actual override fun toString(): String {
        return delegate.stringValue
    }
}
