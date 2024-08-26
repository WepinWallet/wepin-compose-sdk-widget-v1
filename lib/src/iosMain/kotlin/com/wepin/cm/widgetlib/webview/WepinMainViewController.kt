import com.wepin.cm.widgetlib.webview.IOSJSMessageHandler
import com.wepin.cm.widgetlib.webview.WebViewManager
import platform.Foundation.NSBundle
import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.UIKit.UIEditMenuInteractionAnimatingProtocol
import platform.UIKit.UIViewController
import platform.WebKit.WKContextMenuElementInfo
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

    private fun initJsBridge() {
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

    //after recieve message from server
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
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

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun webView(
        webView: WKWebView,
        contextMenuDidEndForElement: WKContextMenuElementInfo
    ) {
        // 이 메서드는 context menu가 종료되었을 때 호출됩니다.
        // 여기에 특별히 처리할 내용이 없다면 빈 메서드로 둡니다.
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun webView(
        webView: WKWebView,
        willPresentEditMenuWithAnimator: UIEditMenuInteractionAnimatingProtocol
    ) {
        // 편집 메뉴가 나타날 때 호출되는 메서드
        // 여기에 특별히 처리할 내용이 없다면 빈 메서드로 둡니다.
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun webView(
        webView: WKWebView,
        didFailProvisionalNavigation: WKNavigation?,
        withError: NSError
    ) {
        // 네비게이션이 실패했을 때 호출되는 메서드
        println("WebView navigation failed with error: ${withError.localizedDescription}")
    }

    override fun viewWillAppear(animated: Boolean) {
        super.viewWillAppear(animated)
    }

    override fun viewDidDisappear(animated: Boolean) {
        super.viewDidDisappear(animated)
        if (WebViewManager.getInstance()._safariVC == null) {
            WebViewManager.getInstance().closeWidget()
        }
    }

    private fun evaluateJavaScript(js: String) {
        WebViewManager.getInstance()._webview!!.evaluateJavaScript(js, null)
    }
}
