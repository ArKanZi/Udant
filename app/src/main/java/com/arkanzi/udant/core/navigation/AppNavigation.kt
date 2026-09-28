package com.arkanzi.udant.core.navigation

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.arkanzi.udant.feature.library.ui.LibraryScreen
import com.arkanzi.udant.feature.library.ui.SavedArticleScreen
import com.arkanzi.udant.feature.search.ui.SearchScreen
import com.arkanzi.udant.feature.settings.ui.SettingsScreen

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
                navigator.openSavedArticles()
                onDestinationConsumed()
            }
        }
    }

    MainScaffold(
        screenChrome = screenChrome,
        selectedKey = currentKey,
        onHomeClick = { navigator.openHome() },
        onSearchClick = { navigator.openSearch() },
        onLibraryClick = { navigator.openLibrary() }
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {

                entry<FeedScreenKey> {
                    FeedScreen(navigator = navigator)
                }

                entry<SearchScreenKey> {
                    SearchScreen()
                }

                entry <LibraryScreenKey>{
                    LibraryScreen(navigator = navigator)
                }

                entry<WebViewScreenKey> {
                    WebViewScreen(url = it.articleUrl, navigator = navigator)
                }

                entry<SavedScreenKey> {
                    SavedArticleScreen(navigator = navigator, target = it.target)
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