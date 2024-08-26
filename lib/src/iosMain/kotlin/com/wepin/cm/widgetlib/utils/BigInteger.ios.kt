package com.wepin.cm.widgetlib.utils

actual class BigInteger actual constructor(value: String) {
    private val delegate = value.toLong()

    actual operator fun div(other: BigInteger): BigInteger {
        return BigInteger((delegate / other.delegate).toString())
    }

    actual operator fun rem(other: BigInteger): BigInteger {
        return BigInteger((delegate % other.delegate).toString())
    }

    actual fun pow(exponent: Int): BigInteger {
        var result = 1L
        repeat(exponent) {
            result *= delegate
        }
        return BigInteger(result.toString())
    }

    actual override fun toString(): String {
        return delegate.toString()
    }
}