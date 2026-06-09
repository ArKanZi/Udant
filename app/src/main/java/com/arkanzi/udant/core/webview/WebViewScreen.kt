package com.arkanzi.udant.core.webview

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.arkanzi.udant.core.navigation.Navigator

@Composable
fun WebViewScreen(
    url: String,
    config: WebViewConfig = WebViewConfig(),
    navigator: Navigator
) {
    val context = LocalContext.current
    val webView = remember { WebViewProvider().create(context = context, config = config) }
    LaunchedEffect(url) {
        webView.loadUrl(url)
    }
    BackHandler {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            navigator.goBack()
        }
    }

    AndroidView(
        factory = {webView}
    )
}
