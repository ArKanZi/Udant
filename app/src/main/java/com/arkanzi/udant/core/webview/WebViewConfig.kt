package com.arkanzi.udant.core.webview

data class WebViewConfig(

    val headers: Map<String, String> =
        emptyMap(),

    val enableCookies: Boolean = true,

    val enableJavascript: Boolean = true
)