import com.multiplatform.webview.jsbridge.WKJsMessageHandler
import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.web.IOSWebView
import com.multiplatform.webview.web.WKNavigationDelegate
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.addProgressObservers
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import com.wepin.cm.widgetlib.storage.AppData
import com.wepin.cm.widgetlib.webview.IOSJSMessageHandler
import com.wepin.cm.widgetlib.webview.JSMessageHandler
import com.wepin.cm.widgetlib.webview.JSResponseHandler
import com.wepin.cm.widgetlib.webview.OpenInBrowserHandler
import com.wepin.cm.widgetlib.webview.WebViewManager
import com.wepin.cm.widgetlib.webview.initJsBridge
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import platform.CoreFoundation.CFErrorRefVar
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSBundle
import platform.Foundation.NSSelectorFromString
import platform.Foundation.NSURL
import platform.Foundation.NSURLAuthenticationChallenge
import platform.Foundation.NSURLCredential
import platform.Foundation.NSURLRequest
import platform.Foundation.NSURLSessionAuthChallengeCancelAuthenticationChallenge
import platform.Foundation.NSURLSessionAuthChallengeDisposition
import platform.Foundation.NSURLSessionAuthChallengeUseCredential
import platform.Foundation.credentialForTrust
import platform.Foundation.serverTrust
import platform.SafariServices.SFSafariViewController
import platform.Security.SecTrustCopyExceptions
import platform.Security.SecTrustEvaluateWithError
import platform.Security.SecTrustSetExceptions
import platform.UIKit.UIApplication
import platform.UIKit.UIBarButtonItem
import platform.UIKit.UIBarButtonItemStyle
import platform.UIKit.UIButton
import platform.UIKit.UIButtonType
import platform.UIKit.UIColor
import platform.UIKit.UIControlEventTouchUpInside
import platform.UIKit.UIControlState
import platform.UIKit.UINavigationBar
import platform.UIKit.UINavigationItem
import platform.UIKit.UIScreen
import platform.UIKit.UIView
import platform.UIKit.UIViewController
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationAction
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKUIDelegateProtocol
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.WebKit.WKWindowFeatures

@Suppress("CONFLICTING_OVERLOADS")
class WepinMainViewController(nibName: String?, bundle: NSBundle?) : UIViewController(
    nibName,
    bundle
), WKUIDelegateProtocol, WKNavigationDelegateProtocol {
    override fun viewDidLoad() {
        super.viewDidLoad()
        WebViewManager.getInstance().bringHimToShow(on = this.view)

        WebViewManager.getInstance()._webview!!.navigationDelegate = this
        WebViewManager.getInstance()._webview!!.userInteractionEnabled = true
        WebViewManager.getInstance()._webview!!.setUIDelegate(this)
        WebViewManager.getInstance()._webview!!.configuration.preferences.javaScriptCanOpenWindowsAutomatically =
            true

        WebViewManager.getInstance()._webview!!.configuration.userContentController.apply {
            addScriptMessageHandler(IOSJSMessageHandler(), "iosJsBridge")
        }
    }

    fun initJsBridge() {
        val initJs =
            """
            console.log("initJsBridge")
            window.kmpJsBridge = {
                callbacks: {},
                callbackId: 0,
                callNative: function (methodName, params, callback) {
                    var message = {
                        methodName: methodName,
                        params: params,
                        callbackId: callback ? window.kmpJsBridge.callbackId++ : -1
                    };
                    if (callback) {
                        window.kmpJsBridge.callbacks[message.callbackId] = callback;
                        console.log('add callback: ' + message.callbackId + ', ' + callback);
                    }
                    window.kmpJsBridge.postMessage(JSON.stringify(message));
                },
                onCallback: function (callbackId, data) {
                    var callback = window.kmpJsBridge.callbacks[callbackId];
                    console.log('onCallback: ' + callbackId + ', ' + data + ', ' + callback);
                    if (callback) {
                        callback(data);
                        delete window.kmpJsBridge.callbacks[callbackId];
                    }
                }
            };
            
            window.kmpJsBridge.postMessage = function (message) {
                    window.webkit.messageHandlers.iosJsBridge.postMessage(message);
                };
            """.trimIndent()
        evaluateJavaScript(initJs)
    }

    override fun webView(
        webView: WKWebView,
        createWebViewWithConfiguration: WKWebViewConfiguration,
        forNavigationAction: WKNavigationAction,
        windowFeatures: WKWindowFeatures
    ): WKWebView? {
        val loadUrl: NSURL? = forNavigationAction.request.URL!!.absoluteURL
        if (loadUrl == null) {
            println("Invalid URL")
            return null
        }

        val safariViewController = WebViewManager.getInstance().getSafariVC(loadUrl)
        this.presentViewController(safariViewController, animated = true, completion = null)
        return null
    }

    override fun webView(
        webView: WKWebView,
        didFinishNavigation: WKNavigation?,
    ) {
        // JavaScript 평가: 페이지가 로드된 후에 실행
        evaluateJavaScript(
            """
                    (function() {
                        const queryString = window.location.search;
                        const params = new URLSearchParams(queryString);
                        params.set('from', 'composeMultiplatform');
                        const newUrl = window.location.pathname + '?' + params.toString();
                        console.log("newUrl11:", newUrl);
                        window.history.pushState({}, '', newUrl);
                    })();
                """.trimIndent()
        )
    }

    //after recieve message from server
    override fun webView(
        webView: WKWebView,
        didCommitNavigation: WKNavigation?,
    ) {
        initJsBridge()

        evaluateJavaScript(
            """
                    (function() {
                        const queryString = window.location.search;
                        const params = new URLSearchParams(queryString);
                        params.set('from', 'composeMultiplatform');
                        const newUrl = window.location.pathname + '?' + params.toString();
                        console.log("newUrl11:", newUrl);
                        window.history.pushState({}, '', newUrl);
                    })();
                """.trimIndent()
        )
    }

//    @OptIn(ExperimentalForeignApi::class)
//    override fun webView(
//        webView: WKWebView,
//        didReceiveAuthenticationChallenge: NSURLAuthenticationChallenge,
//        completionHandler: (NSURLSessionAuthChallengeDisposition, NSURLCredential?) -> Unit
//    ) {
//        CoroutineScope(Dispatchers.Default).launch {
//            val handled = memScoped {
//                val serverTrust = didReceiveAuthenticationChallenge.protectionSpace.serverTrust
//                val exceptions = SecTrustCopyExceptions(serverTrust)
//                if (SecTrustSetExceptions(serverTrust, exceptions)) {
//                    val errorRef = allocArray<CFErrorRefVar>(1)
//                    if (SecTrustEvaluateWithError(serverTrust, errorRef)) {
//                        val credential = NSURLCredential.credentialForTrust(serverTrust)
//                        launch(Dispatchers.Main) {
//                            completionHandler(NSURLSessionAuthChallengeUseCredential, credential)
//                        }
//                        true
//                    } else {
//                        false
//                    }
//                } else {
//                    false
//                }
//            }
//            if (!handled) {
//                launch(Dispatchers.Main) {
//                    completionHandler(NSURLSessionAuthChallengeCancelAuthenticationChallenge, null)
//                }
//            }
//        }
//    }

    override fun viewWillAppear(animated: Boolean) {
        super.viewWillAppear(animated)
    }

    override fun viewDidDisappear(animated: Boolean) {
        super.viewDidDisappear(animated)
        if (WebViewManager.getInstance()._safariVC == null) {
            WebViewManager.getInstance().closeWidget()
        }
    }

    fun evaluateJavaScript(js: String) {
        WebViewManager.getInstance()._webview!!.evaluateJavaScript(js, null)
    }
}
