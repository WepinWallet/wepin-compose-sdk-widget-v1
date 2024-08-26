package com.wepin.cm.widgetlib.webview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.util.KLogSeverity
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewState
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import com.wepin.cm.widgetlib.storage.AppData
import kotlinx.coroutines.flow.filter

@Composable
fun WepinMain() {
    val webViewState = rememberWebViewState(AppData.getWidgetURL())
    val webViewNavigator = rememberWebViewNavigator()
    val jsBridge = rememberWebViewJsBridge(webViewNavigator)

    LaunchedEffect(Unit) {
        initWebView(webViewState)
        initJsBridge(jsBridge)
    }

    WebView(
        state = webViewState,
        modifier = Modifier.fillMaxSize().background(Color.Transparent),
        navigator = webViewNavigator,
        captureBackPresses = false,
        webViewJsBridge = jsBridge,
        onCreated = { nativeWebView ->
            val webViewManager = WebViewManager.getInstance()
            webViewManager.createWebView(nativeWebView)
        },
        onDispose = { nativeWebView ->
            val webViewManager = WebViewManager.getInstance()
            webViewManager.destroyWebview(nativeWebView)
            jsBridge.clear()
        },
    )
    LaunchedEffect(webViewState) {
        snapshotFlow { webViewState.loadingState }
            .filter { it is LoadingState.Loading }
            .collect {
                webViewNavigator.evaluateJavaScript("""
                    (function() {
                        const queryString = window.location.search;
                        const params = new URLSearchParams(queryString);
                        params.set('from', 'composeMultiplatform');
                        const newUrl = window.location.pathname + '?' + params.toString();
                        console.log("newUrl:", newUrl);
                        window.history.pushState({}, '', newUrl);
                    })();
                    
                    window.open = function(url) {
                        window.kmpJsBridge.callNative("open-in-app", JSON.stringify({url: url}), null)
                    }
                """.trimIndent(), null)
            }
    }
    LaunchedEffect(webViewState.errorsForCurrentRequest) {
        snapshotFlow { webViewState.errorsForCurrentRequest.size }
            .collect { newSize ->
                if (newSize > 0) {
                    val latestError = webViewState.errorsForCurrentRequest.last()
                    println("WebView Error: ${latestError.description} (Code: ${latestError.code})")

                    val webViewManager = WebViewManager.getInstance()
                    webViewManager.closeWidget()
                }
            }
    }
}

fun initWebView(webViewState: WebViewState) {
    webViewState.webSettings.apply {
        isJavaScriptEnabled = true
        logSeverity = KLogSeverity.Error
        allowFileAccessFromFileURLs = true
        allowUniversalAccessFromFileURLs = true
        androidWebSettings.apply {
            backgroundColor = Color.Transparent
            allowFileAccess = true
        }
        iOSWebSettings.apply {
            backgroundColor = Color.Transparent
            underPageBackgroundColor = Color.Transparent
            opaque = false
        }
    }
}

fun initJsBridge(webViewJsBridge: WebViewJsBridge) {
    webViewJsBridge.register(JSMessageHandler())
    webViewJsBridge.register(JSResponseHandler())
    webViewJsBridge.register(OpenInBrowserHandler())
}