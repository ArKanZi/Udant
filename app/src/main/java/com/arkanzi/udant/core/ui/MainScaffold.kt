package com.arkanzi.udant.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkanzi.udant.core.model.ScreenChrome
import com.arkanzi.udant.core.ui.components.UdantDefaultAppBar
import com.arkanzi.udant.core.ui.components.UdantFloatingNavBar

@Composable
fun MainScaffold(
    screenChrome: ScreenChrome?,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)

    ) {

        Scaffold(

            modifier = Modifier
                .fillMaxSize()
                .padding(screenChrome?.contentPadding ?: PaddingValues(horizontal = 12.dp)),
            topBar = {
                Column {
                    Spacer(
                        Modifier.windowInsetsTopHeight(WindowInsets.systemBars)
                    )

                    AnimatedContent(
                        targetState = screenChrome?.title != null,
                        label = "TopBar"
                    ) { topBar ->
                        if (topBar && screenChrome?.title != null) {
                            UdantDefaultAppBar(
                                title = screenChrome.title,
                                onBackClick = screenChrome.onBackClick,
                                actions = screenChrome.actions
                            )
                        }
                    }
                }
            },
//            bottomBar = {UdantDefaultNavBar()},
            containerColor = MaterialTheme.colorScheme.surface
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                content()
            }
        }
        AnimatedContent(
            targetState = screenChrome?.showBottomNav == true,
            modifier = Modifier.align(Alignment.BottomCenter),
            label = "BottomBar"
        ) { showBottomNav ->
            if (showBottomNav) {

                UdantFloatingNavBar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                )
            }
        }
    }
}

@Preview
@Composable
fun MainScaffoldPreview() {
    MainScaffold(
        content = {},
        screenChrome = ScreenChrome(
            title = "Udant",
            showBottomNav = true,
            actions = {}
        )
    )
}