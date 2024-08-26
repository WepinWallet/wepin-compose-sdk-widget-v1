package com.wepin.cm.widgetlib.network

import io.ktor.client.HttpClient

expect object KtorWepinClient {
    fun createHttpClient(
        baseUrl: String,
        appDomain: String?,
        appKey: String?,
        version: String?,
    ): HttpClient

    fun closeAllClients()
}