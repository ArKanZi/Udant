package com.arkanzi.udant.core.webview

import android.content.Context
import android.os.Build
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi

class WebViewProvider{

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun create(
        context: Context,
        config: WebViewConfig,
        backgroundColor: Int? = null,
        darkMode: Boolean = false
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
            if (darkMode) {
                settings.isAlgorithmicDarkeningAllowed = true
            }else{
                settings.isAlgorithmicDarkeningAllowed = false
            }
                webViewClient = WebViewClient()

        }

    }

}