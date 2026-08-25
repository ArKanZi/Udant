package com.arkanzi.udant.core.navigation.helper

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.arkanzi.udant.core.model.ScreenChrome
import com.arkanzi.udant.core.navigation.DownloadScreenKey
import com.arkanzi.udant.core.navigation.FeedScreenKey
import com.arkanzi.udant.core.navigation.Navigator
import com.arkanzi.udant.core.navigation.SavedScreenKey
import com.arkanzi.udant.core.navigation.SettingsScreenKey
import com.arkanzi.udant.core.navigation.WebViewScreenKey

@Composable
fun getScreenChrome(
    currentKey: NavKey?,
    navigator: Navigator
): ScreenChrome? {
    return when (currentKey) {

        is FeedScreenKey -> ScreenChrome(
            title = "Udant",
            actions = {
                IconButton(
                    onClick = { navigator.openDownload() }
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Download,
                        contentDescription = "Saved Articles"
                    )
                }


                IconButton(
                    onClick = { navigator.openSaved() }
                ) {

                    Icon(
                        imageVector = Icons.Outlined.BookmarkBorder,
                        contentDescription = "Saved Articles"
                    )
                }

                IconButton(
                    onClick = { navigator.openSettings() }
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings"
                    )
                }
            },
            showBottomNav = true
        )

        is SavedScreenKey -> ScreenChrome(
            title = "Saved",
            actions = {
                IconButton(
                    onClick = { navigator.openDownload() }
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Download,
                        contentDescription = "Saved Articles"
                    )
                }


                IconButton(
                    onClick = { navigator.openSaved() }
                ) {

                    Icon(
                        imageVector = Icons.Outlined.BookmarkBorder,
                        contentDescription = "Saved Articles"
                    )
                }

                IconButton(
                    onClick = { navigator.openSettings() }
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings"
                    )
                }
            },
            showBottomNav = true
        )

        is SettingsScreenKey -> ScreenChrome(
            onBackClick = { navigator.goBack() },
            title = "Settings",
            showBottomNav = false
        )

        is DownloadScreenKey -> ScreenChrome(
            title = "Download",
            actions = {
                IconButton(
                    onClick = { navigator.openDownload() }
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Download,
                        contentDescription = "Saved Articles"
                    )
                }


                IconButton(
                    onClick = { navigator.openSaved() }
                ) {

                    Icon(
                        imageVector = Icons.Outlined.BookmarkBorder,
                        contentDescription = "Saved Articles"
                    )
                }

                IconButton(
                    onClick = { navigator.openSettings() }
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings"
                    )
                }
            },
            showBottomNav = false
        )

        is WebViewScreenKey -> ScreenChrome(
            title = "WebView",
            onBackClick = { navigator.goBack() },
            contentPadding = PaddingValues(horizontal = 0.dp),
            showBottomNav = false
        )

        else -> null
    }
}