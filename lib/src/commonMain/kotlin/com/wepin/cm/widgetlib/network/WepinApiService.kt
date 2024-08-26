package com.wepin.cm.widgetlib.network

import com.wepin.cm.widgetlib.types.GetAccountListRequest
import com.wepin.cm.widgetlib.types.RegisterRequest
import com.wepin.cm.widgetlib.types.UpdateTermsAcceptedRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.request.headers
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.contentType

interface WepinApiService {
    suspend fun getAppAccountList(accessToken: String, params: GetAccountListRequest): HttpResponse
    suspend fun getAccountBalance(accessToken: String, accountId: String): HttpResponse
    suspend fun getNFTList(accessToken: String, walletId: String, userId: String): HttpResponse
    suspend fun refreshNFTList(accessToken: String, walletId: String, userId: String): HttpResponse
    suspend fun register(accessToken: String, data: RegisterRequest): HttpResponse
    suspend fun updateTermsAccepted(accessToken: String, userId: String, parameter: UpdateTermsAcceptedRequest): HttpResponse
}

fun createWepinApiService(okHttpClient: HttpClient): WepinApiService =
    object: WepinApiService {
        override suspend fun getAppAccountList(accessToken: String, params: GetAccountListRequest): HttpResponse {
            return okHttpClient.get("account") {
                headers {
                    append("Authorization", "Bearer $accessToken")
                }
                url {
                    parameters.append("walletId", params.walletId)
                    parameters.append("userId", params.userId)
                    parameters.append("localeId", params.localeId)
                }
            }
        }

        override suspend fun getAccountBalance(accessToken: String, accountId: String): HttpResponse {
            return okHttpClient.get("accountbalance/${accountId}/balance") {
                headers {
                    append("Authorization", "Bearer $accessToken")
                }
            }
        }

        override suspend fun getNFTList(accessToken: String, walletId: String, userId: String): HttpResponse {
            return okHttpClient.get("nft") {
                headers {
                    append("Authorization", "Bearer $accessToken")
                }
                url {
                    parameters.append("walletId", walletId)
                    parameters.append("userId", userId)
                }
            }
        }

        override suspend fun refreshNFTList(accessToken: String, walletId: String, userId: String): HttpResponse {
            return okHttpClient.get("nft/refresh") {
                headers {
                    append("Authorization", "Bearer $accessToken")
                }
                url {
                    parameters.append("walletId", walletId)
                    parameters.append("userId", userId)
                }
            }
        }

        override suspend fun register(accessToken: String, data: RegisterRequest): HttpResponse {
            return okHttpClient.post("app/register") {
                headers {
                    append("Authorization", "Bearer $accessToken")
                }
                contentType(Json)
                setBody(data)
            }
        }

        override suspend fun updateTermsAccepted(
            accessToken: String,
            userId: String,
            parameter: UpdateTermsAcceptedRequest
        ): HttpResponse {
            return okHttpClient.patch("user/$userId/terms-accepted") {
                headers {
                    append("Authorization", "Bearer $accessToken")
                }
                contentType(Json)
                setBody(parameter)
            }
        }

    }