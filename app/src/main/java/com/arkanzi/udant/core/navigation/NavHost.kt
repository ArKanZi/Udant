package com.arkanzi.udant.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.arkanzi.udant.core.ui.MainScaffold
import com.arkanzi.udant.core.webview.WebViewScreen
import com.arkanzi.udant.feature.feed.ui.FeedScreen
import com.arkanzi.udant.feature.saved.ui.SavedScreen
import com.arkanzi.udant.feature.settings.ui.SettingsScreen

@Composable
fun NavHost() {
    val startKey = FeedScreenKey
    val backStack = rememberNavBackStack(startKey)
    val navigator = remember { Navigator(backStack) }

    MainScaffold(
        onSavedClick = { navigator.openSaved() },
        onSettingsClick = {navigator.openSettings() }
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

                entry<SavedScreenKey>{
                    SavedScreen(navigator = navigator)
                }

                entry<SettingsScreenKey>{
                    SettingsScreen()
                }
            }
        )
    }
}