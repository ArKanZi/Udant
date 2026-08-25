package com.arkanzi.udant.core.model

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

data class ScreenChrome(
    val title: String,
    val onBackClick: (() -> Unit)? = null,
    val showBottomNav: Boolean = false,
    val contentPadding: PaddingValues? = PaddingValues(horizontal = 12.dp),
    val actions: @Composable RowScope.() -> Unit = {}
)