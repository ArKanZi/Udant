package com.arkanzi.udant.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

class Navigator(
    private val backStack: NavBackStack<NavKey>
) {

    fun goBack() {
        backStack.removeLastOrNull()
    }

    fun openHome(){
        if (
            backStack.lastOrNull() != FeedScreenKey
        ) {
            backStack.add(
                FeedScreenKey
            )
        }
    }

    fun openSearch() {
        if (
            backStack.lastOrNull() != SearchScreenKey
        ) {
            backStack.add(
                SearchScreenKey
            )
        }
    }

    fun openLibrary() {
        if (
            backStack.lastOrNull() != LibraryScreenKey
        ) {
            backStack.add(
                LibraryScreenKey
            )
        }
    }

    fun openWebView(url: String) {
        if (
            backStack.lastOrNull() != SettingsScreenKey
        ) {
            backStack.add(
                WebViewScreenKey(url)
            )
        }
    }

    fun openSettings() {
        if (
            backStack.lastOrNull() != SettingsScreenKey
        ) {
            backStack.add(
                SettingsScreenKey
            )
        }
    }

    fun openSaved() {

        if (
            backStack.lastOrNull() != SavedScreenKey
        ) {

            backStack.add(
                SavedScreenKey
            )
        }
    }

    fun openDownload() {
        if (
            backStack.lastOrNull() != DownloadScreenKey
        ) {

            backStack.add(
                DownloadScreenKey
            )
        }
    }
}