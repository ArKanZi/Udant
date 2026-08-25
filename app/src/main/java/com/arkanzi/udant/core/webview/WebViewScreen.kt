package com.arkanzi.udant.core.webview

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.arkanzi.udant.core.navigation.Navigator

@Composable
fun WebViewScreen(
    url: String,
    config: WebViewConfig = WebViewConfig(),
    navigator: Navigator
) {
    val surfaceColor = MaterialTheme.colorScheme.surface.toArgb()
    val context = LocalContext.current
    val webView = remember {
        WebViewProvider().create(
            context = context,
            config = config,
            backgroundColor = surfaceColor
        )
    }
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
        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        factory = { webView }
    )
}
