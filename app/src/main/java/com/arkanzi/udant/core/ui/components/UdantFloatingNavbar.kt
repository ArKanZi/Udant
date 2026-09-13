package com.arkanzi.udant.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.arkanzi.udant.R
import com.arkanzi.udant.core.navigation.FeedScreenKey
import com.arkanzi.udant.core.navigation.LibraryScreenKey
import com.arkanzi.udant.core.navigation.SearchScreenKey

@Composable
fun UdantFloatingNavBar(
    selectedKey: NavKey?,
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onLibraryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(40.dp))
            .background(Color.Transparent)
            .padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigationBar(
            modifier = Modifier.clip(CircleShape),
            tonalElevation = 0.dp,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            windowInsets = WindowInsets(0, 0, 0, 0),
        ) {
            NavigationBarItem(
                selected = selectedKey == FeedScreenKey,
                onClick = onHomeClick,
                icon = {
                    Icon(

                        painter = painterResource(
                            if (selectedKey == FeedScreenKey) {
                                R.drawable.ic_home_selected
                            } else {
                                R.drawable.ic_home
                            }
                        ),
                        contentDescription = "Home"
                    )
                },
                modifier = Modifier.scale(0.90f),
                enabled = true,
                label = { Text(
                    text = "Home",
                    fontWeight = if (selectedKey == FeedScreenKey) {
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
                selected = selectedKey == SearchScreenKey,
                onClick = onSearchClick,
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = "Search"
                    )
                },
                modifier = Modifier.scale(0.90f),
                enabled = true,
                label = { Text(
                    text = "Search",
                    fontWeight = if (selectedKey == SearchScreenKey) {
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
                selected = selectedKey == LibraryScreenKey,
                onClick = onLibraryClick,
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_library),
                        contentDescription = "Library"
                    )
                },
                modifier = Modifier.scale(0.90f),
                enabled = true,
                label = { Text(
                    text = "Library",
                    fontWeight = if (selectedKey == LibraryScreenKey) {
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
}

@Preview(showBackground = true)
@Composable
private fun UdantFloatingNavBarPreview() {
    UdantFloatingNavBar(
        selectedKey = SearchScreenKey,
        onHomeClick = {},
        onSearchClick = {},
        onLibraryClick = {},

    )
}