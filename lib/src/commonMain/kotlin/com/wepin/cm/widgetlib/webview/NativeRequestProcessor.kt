package com.wepin.cm.widgetlib.webview

import com.wepin.cm.loginlib.storage.WepinStorageManager
import com.wepin.cm.loginlib.types.LoginOauth2Params
import com.wepin.cm.widgetlib.WepinWidgetSDK
import com.wepin.cm.widgetlib.storage.AppData
import com.wepin.cm.widgetlib.types.Command
import com.wepin.cm.widgetlib.types.JSGetLoginInfoRequestParameter
import com.wepin.cm.widgetlib.types.JSRequest
import com.wepin.cm.widgetlib.utils.getDomain
import com.wepin.cm.widgetlib.utils.getPlatform
import com.wepin.cm.widgetlib.types.JSResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class NativeRequestProcessor {
    fun dispatcher(requestCode: Int, request: JSRequest): JSResponse? {
        val command: String = request.body.command

        when (command) {
            Command.CMD_READY_TO_WIDGET -> return readyToWidget(request)
            Command.CMD_SET_LOCAL_STORAGE -> return setLocalStorage(request)
            Command.CMD_SET_USER_EMAIL -> return setUserEmail(request)
            Command.CMD_GET_SDK_REQUEST -> return getSdkRequest(request)
            Command.CMD_CLOSE_WEPIN_WIDGET -> {
                return closeWepinWidget(request)
            }
            else -> throw Error("")
        }
    }

    private fun readyToWidget(request: JSRequest): JSResponse {
        var jsResponse: JSResponse? = null

        try {
            val builder: JSResponse.Builder = JSResponse.Builder(request.header.request_to, request.body.command, "SUCCESS", request.header.id)
            builder.setAppKey(AppData.getAppKey())
            builder.setDomain(getDomain())
            builder.setPlatform(getPlatform())
            builder.setAttributes(AppData.getAttributes()!!)
            builder.setType()
            builder.setVersion()
            builder.setLocalData()
            jsResponse = builder.build()
            return jsResponse
        } catch(e: Exception) {
            throw e
        }
    }
    private fun setLocalStorage(request: JSRequest): JSResponse? {
        val parameter = request.body.parameter
        if (parameter is Map<*, *>) {
            val dataMap = parameter as? Map<String, Any>
            if (dataMap != null) {
                WepinStorageManager.setAllStorage(dataMap)
                if (dataMap.containsKey("user_info")) {
                    CoroutineScope(Dispatchers.IO).launch {
                        WepinWidgetSDK.getInstance().getStatus()
                        WebViewResponseManager.loginDeferred?.complete(true)
                    }
                }
            } else {
                println("Failed to cast parameter to Map<String, Any>")
            }
        } else {
            println("parameter is not a Map")
        }

        var jsResponse: JSResponse? = null
        try {
            val builder: JSResponse.Builder = JSResponse.Builder(
                request.header.request_to,
                request.body.command,
                "SUCCESS",
                request.header.id
            )

            jsResponse = builder.build()
        } catch(e: Exception) {
            println("$e")
            throw e
        }
        return jsResponse
    }
    private fun setUserEmail(request: JSRequest): JSResponse? {
        var jsResponse: JSResponse? = null

        try {
            val builder: JSResponse.Builder = JSResponse.Builder(request.header.request_to, request.body.command, "SUCCESS", request.header.id)
            builder.setEmail()
            jsResponse = builder.build()
        } catch(e: Exception) {
            println("$e")
        }
        return jsResponse
    }
    private fun getSdkRequest(request: JSRequest): JSResponse? {
        var jsResponse: JSResponse? = null

        try {
            val builder: JSResponse.Builder = JSResponse.Builder(request.header.request_to, request.body.command, "SUCCESS", request.header.id)
            val header = SDKRequest.getSDKRequestHeader()
            val body = SDKRequest.getSDKRequestBody()
            if (header != null && body != null) {
                builder.setSubRequestHeader(header)
                builder.setSubRequestBody(body)
            } else {
//                builder.setString("No request")
            }

            jsResponse = builder.build()
            return jsResponse
        } catch(e: Exception) {
            println("$e")

        }
        return null
    }


    suspend fun getLoginInfo(request: JSRequest):JSResponse? {
        val providerName = (request.body.parameter as JSGetLoginInfoRequestParameter).provider
        val providerInfo = AppData.getProviderList(providerName)
        val params = LoginOauth2Params(
            provider = providerInfo?.provider ?: "",
            clientId = providerInfo?.clientId ?: ""
        )

        return _loginFirebase(params, request)
    }

    private suspend fun _loginFirebase(params: LoginOauth2Params, request: JSRequest): JSResponse {
        try {
            val builder: JSResponse.Builder = JSResponse.Builder(request.header.request_to, request.body.command, "SUCCESS", request.header.id)
            val res = WepinWidgetSDK.getInstance().login?.loginFirebaseWithOauthProvider(params)
            if (res != null) {
                builder.setLoginResult(res)
            } else {
                builder.setStringResponse("failed login")
            }
            return builder.build()

        } catch (e: Exception) {
            val errorBuilder = JSResponse.Builder(request.header.request_to, request.body.command, "ERROR", request.header.id)

            errorBuilder.setErrorResponse("$e")
            return errorBuilder.build()
        }
    }

    private fun closeWepinWidget(request: JSRequest): JSResponse? {
        val webViewManager: WebViewManager = WebViewManager.getInstance()

        webViewManager.closeWidget()

//        var jsResponse: JSResponse? = null
//        try {
//            val builder: JSResponse.Builder = JSResponse.Builder(
//                request.header.request_to,
//                request.body.command,
//                "SUCCESS",
//                request.header.id
//            )
//
//            jsResponse = builder.build()
//        } catch(e: Exception) {
//            println("$e")
//            throw e
//        }
//        return jsResponse
        return null
    }
}