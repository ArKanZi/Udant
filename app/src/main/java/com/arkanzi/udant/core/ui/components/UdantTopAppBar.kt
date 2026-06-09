package com.arkanzi.udant.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.outlined.Refresh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UdantTopAppBar(

    onSavedClick: () -> Unit = {},

    onSettingsClick: () -> Unit = {},


) {

    TopAppBar(

        title = {

            Text(
                text = "Udant",
                style = MaterialTheme.typography.titleLarge
            )
        },

        actions = {


            IconButton(
                onClick = onSavedClick
            ) {

                Icon(
                    imageVector = Icons.Outlined.BookmarkBorder,
                    contentDescription = "Saved Articles"
                )
            }

            IconButton(
                onClick = onSettingsClick
            ) {

                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings"
                )
            }
        },

        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}