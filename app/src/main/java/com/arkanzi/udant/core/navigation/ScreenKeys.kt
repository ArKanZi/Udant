package com.arkanzi.udant.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface UdantNavKey : NavKey

@Serializable
data object FeedScreenKey: UdantNavKey
@Serializable
data object SearchScreenKey: UdantNavKey
@Serializable
data object LibraryScreenKey: UdantNavKey

@Serializable
data class WebViewScreenKey(
    val articleUrl:String,
    val header: Map<String, String> = emptyMap()
): UdantNavKey

@Serializable
data object SavedScreenKey: UdantNavKey

@Serializable
data object DownloadScreenKey: UdantNavKey

@Serializable
data object SettingsScreenKey: UdantNavKey