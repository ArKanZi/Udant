package com.arkanzi.udant.core.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkanzi.udant.R


@Composable
fun UdantDefaultNavBar() {
    val selected = true
    NavigationBar(
        modifier = Modifier,
        tonalElevation = 0.dp,
        containerColor = MaterialTheme.colorScheme.surfaceContainer

        ) {
        NavigationBarItem(
            selected = selected,
            onClick = {},
            icon = {
                Icon(
                    painter = painterResource(
                        if (selected) {
                            R.drawable.ic_home_selected
                        } else {
                            R.drawable.ic_home
                        }
                    ),
                    contentDescription = "Home"
                )
            },
            modifier = Modifier,
            enabled = true,
            label = { Text(
                text = "Home",
                fontWeight = if (selected) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Normal
                }
            ) },
            alwaysShowLabel = true,
            colors = NavigationBarItemDefaults
                .colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                ),
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = "Search"
                )
            },
            enabled = true,
            label = { Text(
                text = "Search",
                fontWeight = if (selected) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Normal
                }
            ) },
            alwaysShowLabel = true,
            colors = NavigationBarItemDefaults
                .colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                ),
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_library),
                    contentDescription = "Library"
                )
            },
            modifier = Modifier,
            enabled = true,
            label = { Text(
                text = "Library",
                fontWeight = if (selected) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Normal
                }
            ) },
            alwaysShowLabel = true,
            colors = NavigationBarItemDefaults
                .colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UdantDefaultNavBarPreview() {
    UdantDefaultNavBar()
}