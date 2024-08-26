package com.wepin.cm.widgetlib.network

import com.wepin.cm.loginlib.error.WepinError
import com.wepin.cm.loginlib.types.KeyType
import com.wepin.cm.widgetlib.storage.AppData
import com.wepin.cm.widgetlib.types.GetAccountBalanceResponse
import com.wepin.cm.widgetlib.types.GetAccountListRequest
import com.wepin.cm.widgetlib.types.GetAccountListResponse
import com.wepin.cm.widgetlib.types.GetNFTRequest
import com.wepin.cm.widgetlib.types.GetNFTResponse
import com.wepin.cm.widgetlib.types.RegisterRequest
import com.wepin.cm.widgetlib.types.RegisterResponse
import com.wepin.cm.widgetlib.types.UpdateTermsAccepedResponse
import com.wepin.cm.widgetlib.types.UpdateTermsAcceptedRequest
import com.wepin.cm.widgetlib.utils.getDomain
import com.wepin.cm.widgetlib.utils.getVersionMetaDataValue
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class WepinNetworkManager() {
    var wepinBaseUrl: String? = null
    private var _appKey: String? = null
    private var _domain: String? = null
    private var _version: String? = null

    private var httpClient: HttpClient? = null
    private var wepinApiService: WepinApiService? = null

    init {
        _appKey = AppData.getAppKey()
        _domain = getDomain()
        _version = getVersionMetaDataValue()
        wepinBaseUrl = getSdkUrl(apiKey = _appKey!!)

        httpClient = KtorWepinClient.createHttpClient(wepinBaseUrl!!, _appKey, _domain, _version)
        wepinApiService = createWepinApiService(httpClient!!)
    }

    suspend fun getAppAccountList(accessToken: String, params: GetAccountListRequest): GetAccountListResponse {
        val result = withContext(Dispatchers.IO) {
            val response: HttpResponse = wepinApiService!!.getAppAccountList(accessToken, params)
            if (response.status >= HttpStatusCode.OK && response.status < HttpStatusCode.MultipleChoices) {
                val data: GetAccountListResponse = response.body()
                data
            } else {
                throw Exception("HTTP ${response.status.value}: ${response.bodyAsText()}")
            }
        }
        return result
    }

    suspend fun getAccountBalance(accessToken: String, accountId: String): GetAccountBalanceResponse {
        val result = withContext(Dispatchers.IO) {
            val response: HttpResponse = wepinApiService!!.getAccountBalance(accessToken, accountId)
            if (response.status >= HttpStatusCode.OK && response.status < HttpStatusCode.MultipleChoices) {
                val data: GetAccountBalanceResponse = response.body()
                data
            } else {
                throw Exception("HTTP ${response.status.value}: ${response.bodyAsText()}")
            }
        }
        return result
    }

    suspend fun getNFTs(accessToken: String, params: GetNFTRequest): GetNFTResponse {
        val result = withContext(Dispatchers.IO) {
            val response: HttpResponse = wepinApiService!!.getNFTList(accessToken = accessToken, walletId = params.walletId, userId = params.userId)
            if (response.status >= HttpStatusCode.OK && response.status < HttpStatusCode.MultipleChoices) {
                val data: GetNFTResponse = response.body()
                data
            } else {
                throw Exception("HTTP ${response.status.value}: ${response.bodyAsText()}")
            }
        }
        return result
    }

    suspend fun refreshNFTs(accessToken: String, params: GetNFTRequest): GetNFTResponse {
        val result = withContext(Dispatchers.IO) {
            val response: HttpResponse = wepinApiService!!.refreshNFTList(accessToken = accessToken, walletId = params.walletId, userId = params.userId)
            if (response.status >= HttpStatusCode.OK && response.status < HttpStatusCode.MultipleChoices) {
                val data: GetNFTResponse = response.body()
                data
            } else {
                throw Exception("HTTP ${response.status.value}: ${response.bodyAsText()}")
            }
        }
        return result
    }

    suspend fun register(accessToken: String, parameter: RegisterRequest): RegisterResponse {
        val result = withContext(Dispatchers.IO) {
            val response: HttpResponse = wepinApiService!!.register(accessToken = accessToken, parameter)
            if (response.status >= HttpStatusCode.OK && response.status < HttpStatusCode.MultipleChoices) {
                val data: RegisterResponse = response.body()
                data
            } else {
                throw Exception("HTTP ${response.status.value}: ${response.bodyAsText()}")
            }
        }
        return result
    }

    suspend fun updateTermsAccepted(accessToken: String, userId: String, parameter: UpdateTermsAcceptedRequest): UpdateTermsAccepedResponse {
        val result = withContext(Dispatchers.IO) {
            val response: HttpResponse = wepinApiService!!.updateTermsAccepted(accessToken, userId, parameter)
            if (response.status >= HttpStatusCode.OK && response.status < HttpStatusCode.MultipleChoices) {
                val data: UpdateTermsAccepedResponse = response.body()
                data
            } else {
                throw Exception("HTTP ${response.status.value}: ${response.bodyAsText()}")
            }
        }
        return result
    }

    private fun getSdkUrl(apiKey: String): String {
        return when (KeyType.fromAppKey(apiKey)) {
            KeyType.DEV -> {
                "https://dev-sdk.wepin.io/v1/"
            }

            KeyType.STAGE -> {
                "https://stage-sdk.wepin.io/v1/"
            }

            KeyType.PROD -> {
                "https://sdk.wepin.io/v1/"
            }

            else -> {
                throw WepinError.INVALID_APP_KEY
            }
        }
    }
}