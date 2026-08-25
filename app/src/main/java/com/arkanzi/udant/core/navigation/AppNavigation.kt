package com.arkanzi.udant.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.arkanzi.udant.core.job.download.ui.DownloadScreen
import com.arkanzi.udant.core.navigation.helper.getScreenChrome
import com.arkanzi.udant.core.ui.MainScaffold
import com.arkanzi.udant.core.webview.WebViewScreen
import com.arkanzi.udant.feature.feed.ui.FeedScreen
import com.arkanzi.udant.feature.savedArticles.ui.SavedArticleScreen
import com.arkanzi.udant.feature.settings.ui.SettingsScreen

@Composable
fun AppNavigation(destination: String?, onDestinationConsumed: () -> Unit) {
    val startKey = FeedScreenKey
    val backStack = rememberNavBackStack(startKey)
    val navigator = remember { Navigator(backStack) }

    val currentKey = backStack.lastOrNull()


    val screenChrome = getScreenChrome(
        currentKey = currentKey,
        navigator = navigator
    )


    LaunchedEffect(destination) {


        when (destination) {

            "saved" -> {
                navigator.openSaved()
                onDestinationConsumed()
            }
        }
    }

    MainScaffold(
        screenChrome = screenChrome
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {

                entry<FeedScreenKey> {
                    FeedScreen(navigator = navigator)
                }

                entry<WebViewScreenKey> {
                    WebViewScreen(url = it.articleUrl, navigator = navigator)
                }

                entry<SavedScreenKey> {
                    SavedArticleScreen(navigator = navigator)
                }

                entry<DownloadScreenKey> {
                    DownloadScreen()
                }

                entry<SettingsScreenKey> {
                    SettingsScreen()
                }
            }
        )
    }
}