package com.wepin.cm.widgetlib.webview

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.multiplatform.webview.web.NativeWebView
import com.wepin.cm.widgetlib.const.Constants
import com.wepin.cm.widgetlib.storage.AppData


actual class WebViewManager() {
    private var context: Context? = null
    actual companion object {
        var _instance: WebViewManager? = null
        actual fun getInstance(): WebViewManager {
            if (_instance == null) {
                _instance = WebViewManager()
            }
            return _instance as WebViewManager
        }
    }

    actual fun openWidget() {
        openActivity()
    }

    actual fun destroyWebview(webview: NativeWebView) {
        webview.stopLoading()
        webview.webChromeClient = null
        webview.clearHistory()
        webview.clearCache(true)
        webview.removeAllViews()
        webview.destroy()
    }

    actual fun createWebView(webview: NativeWebView) {
    }

    private fun openActivity() {
        val context = AppData.getContext() as Context
        val intent = Intent(context, WebViewActivity::class.java).apply {}
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION)
        intent.putExtra(Constants.KEY_RETURN_FROM_APP_MAIN, true)
        context.startActivity(intent)
    }

    actual fun closeWidget() {
        if (context is WebViewActivity) {
            (context as WebViewActivity).finish() // Activity 종료
        }
    }

    fun clearContext() {
        this.context = null
    }

    fun setContext(context: Context) {
        this.context = context
    }
}

class WebViewActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setDimAmount(0f)
        WebViewManager.getInstance().setContext(this)
        setContent {
            WepinMain()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Activity가 종료될 때 context 해제
        WebViewManager.getInstance().clearContext()
    }
}