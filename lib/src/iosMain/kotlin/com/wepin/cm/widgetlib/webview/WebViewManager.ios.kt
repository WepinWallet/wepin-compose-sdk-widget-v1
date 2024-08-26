package com.wepin.cm.widgetlib.webview

import WepinMainViewController
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.window.ComposeUIViewController
import com.multiplatform.webview.web.NativeWebView
import com.wepin.cm.widgetlib.const.WidgetUrl
import com.wepin.cm.widgetlib.storage.AppData
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectZero
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UIModalPresentationOverFullScreen
import platform.UIKit.UINavigationController
import platform.UIKit.UIScreen
import platform.UIKit.UIView
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.removeFromParentViewController
import platform.UIKit.statusBarManager
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration

actual class WebViewManager() {
    var window: UIWindow = UIApplication.sharedApplication.windows.first() as UIWindow
    var _webview: WKWebView? = null
    var _safariVC: SFSafariViewController? = null
    var viewController: UIViewController? = null

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
        viewWebStart(AppData.getWidgetURL())

        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController

        val vcWebIn = WepinMainViewController(nibName = null, bundle =  null)
        vcWebIn.modalPresentationStyle = UIModalPresentationOverFullScreen
        rootViewController!!.presentViewController(vcWebIn, animated = true, completion = null)
    }

    actual fun closeWidget() {
        if (_webview != null) {
            _webview!!.removeConstraints(_webview!!.constraints)
            _webview!!.stopLoading()
            _webview!!.removeFromSuperview()
            _webview!!.navigationDelegate = null
            _webview!!.setUIDelegate(null)
            if (_safariVC != null) {
                closeSafariVC()
            }
            val keyWindow = UIApplication.sharedApplication.keyWindow
            val rootViewController = keyWindow?.rootViewController?.presentedViewController
            rootViewController?.dismissViewControllerAnimated(true, completion = null)
            viewController = null
            _webview = null
        }
    }

    actual fun createWebView(webview: NativeWebView) {
        println("createWebView")
    }

    actual fun destroyWebview(webview: NativeWebView) {
        if (_webview != null) {
            _webview!!.stopLoading()
            _webview!!.removeFromSuperview()
            _webview!!.navigationDelegate = null
            _webview!!.setUIDelegate(null)
            _webview = null
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun viewWebStart(urlString: String) {
        if (_webview != null) {
            println("viewWeb is not nil")
            closeWidget()
        }

        val wVConfig = WKWebViewConfiguration()
        wVConfig.applicationNameForUserAgent = wVConfig.applicationNameForUserAgent + "io.wepin"
        wVConfig.preferences.javaScriptCanOpenWindowsAutomatically = true

        val vW = WKWebView(frame= CGRectZero.readValue() , configuration = wVConfig)

        vW.translatesAutoresizingMaskIntoConstraints = false

        window.addSubview(vW)
        window.sendSubviewToBack(vW)

        _webview = vW

        val url = NSURL(string = urlString)
        val req = NSURLRequest(url)

        _webview!!.loadRequest(req)
    }

    @OptIn(ExperimentalForeignApi::class)
    fun bringHimToShow(on: UIView) {

        _webview?.let { on.addSubview(it) }

        val statusBarHeight = window.windowScene?.statusBarManager?.statusBarFrame?.useContents {
            size.height
        }
        var fixedHeight = on.frame.useContents { size.height }
        val minus = fixedHeight.minus(statusBarHeight!!)
        _webview!!.inspectable = true

        _webview!!.removeConstraints(_webview!!.constraints)
        _webview!!.bottomAnchor.constraintEqualToAnchor(on.bottomAnchor).active = true
        _webview!!.leftAnchor.constraintEqualToAnchor(on.safeAreaLayoutGuide.leftAnchor).active = true
        _webview!!.rightAnchor.constraintEqualToAnchor(on.safeAreaLayoutGuide.rightAnchor).active = true
        _webview!!.heightAnchor.constraintEqualToConstant(minus).active = true

        //  웹뷰 배경화면 투명처리
        _webview!!.backgroundColor = UIColor.clearColor
        _webview!!.opaque = false
        //
        _webview!!.configuration.preferences.javaScriptCanOpenWindowsAutomatically = true //window.close에 필요!!

    }

    fun getSafariVC(url: NSURL): SFSafariViewController {
        if (_safariVC != null) {
            closeSafariVC()
        }
        val safariViewController = SFSafariViewController(url)
        _safariVC = safariViewController
        return _safariVC!!
    }

    fun closeSafariVC() {
        if (_safariVC != null) {
            _safariVC?.removeFromParentViewController()
            _safariVC?.dismissViewControllerAnimated(flag = true, completion = null)
            _safariVC?.view!!.removeFromSuperview()
            _safariVC = null
        }
    }
}
