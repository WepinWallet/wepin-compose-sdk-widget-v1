package com.wepin.cm.widgetlib.utils

expect class BigInteger(value: String) {
    operator fun div(other: BigInteger): BigInteger
    operator fun rem(other: BigInteger): BigInteger
    fun pow(exponent: Int): BigInteger
    override fun toString(): String
}