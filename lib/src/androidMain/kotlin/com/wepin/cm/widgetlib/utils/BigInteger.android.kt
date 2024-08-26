package com.wepin.cm.widgetlib.utils

import java.math.BigInteger as JvmBigInteger

actual class BigInteger actual constructor(value: String) {
    private val delegate = JvmBigInteger(value)

    actual operator fun div(other: BigInteger): BigInteger {
        return BigInteger(delegate.divide(JvmBigInteger(other.toString())).toString())
    }

    actual operator fun rem(other: BigInteger): BigInteger {
        return BigInteger(delegate.remainder(JvmBigInteger(other.toString())).toString())
    }

    actual fun pow(exponent: Int): BigInteger {
        return BigInteger(delegate.pow(exponent).toString())
    }

    actual override fun toString(): String {
        return delegate.toString()
    }
}