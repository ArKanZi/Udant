package com.arkanzi.udant.core.webview

import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient

class WebViewProvider{

    fun create(
        context: Context,
        config: WebViewConfig,
        backgroundColor: Int? = null
    ):WebView{
        return WebView(context).apply {
            backgroundColor?.let {
                setBackgroundColor(it)
            }
                CookieManager
                    .getInstance()
                    .setAcceptCookie(config.enableCookies)

                settings.javaScriptEnabled = config.enableJavascript

                settings.domStorageEnabled = true

                webViewClient = WebViewClient()

        }

    }

}