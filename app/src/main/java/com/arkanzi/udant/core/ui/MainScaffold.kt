package com.arkanzi.udant.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkanzi.udant.core.navigation.Navigator
import com.arkanzi.udant.core.ui.components.UdantTopAppBar

@Composable
fun MainScaffold(
    onSavedClick: () -> Unit,

    onSettingsClick: () -> Unit,

    content: @Composable () -> Unit
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            UdantTopAppBar(
                onSavedClick = onSavedClick,
                onSettingsClick = onSettingsClick
            )
        },
        containerColor = Color.Black,
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) { content() }

        })
}