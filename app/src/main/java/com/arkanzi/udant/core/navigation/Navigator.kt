package com.arkanzi.udant.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

class Navigator(
    private val backStack: NavBackStack<NavKey>
) {

    fun goBack() {
        backStack.removeLastOrNull()
    }

    fun openWebView(url: String) {
        backStack.add(
            WebViewScreenKey(url)
        )
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
}