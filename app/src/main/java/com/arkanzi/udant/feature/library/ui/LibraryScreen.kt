package com.arkanzi.udant.feature.library.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.rememberNavBackStack
import com.arkanzi.udant.core.navigation.FeedScreenKey
import com.arkanzi.udant.core.navigation.Navigator
import com.arkanzi.udant.core.ui.components.SearchBar
import com.arkanzi.udant.feature.library.ui.views.CollectionView
import com.arkanzi.udant.feature.library.viewmodel.LibraryOrder
import com.arkanzi.udant.feature.library.viewmodel.LibraryView
import com.arkanzi.udant.feature.library.viewmodel.LibraryViewModel
import com.arkanzi.udant.feature.library.ui.components.shared_components.SortOption

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    libraryViewModel: LibraryViewModel = hiltViewModel(),
    navigator: Navigator
) {
    val view = libraryViewModel.viewBy

    var query by remember {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        SearchBar(
            query = query,
            onQueryChange = { query = it },
            placeHolder = "Search your Library...",
            onSearchClick = {},
        )

        Spacer(modifier = Modifier.size(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SortOption(
                label = "View",
                value = view.displayName(),
                options = LibraryView.entries.map { it.displayName() },
                onOptionSelected = {
                    libraryViewModel.selectView(
                        LibraryView.entries.first { value ->
                            value.displayName() == it
                        }
                    )
                },
                modifier = Modifier.weight(1f)
            )

            SortOption(
                label = "Order By",
                value = libraryViewModel.order.displayName(),
                options = LibraryOrder.entries.map { it.displayName() },
                onOptionSelected = {
                    libraryViewModel.selectOrder(
                        LibraryOrder.entries.first { value ->
                            value.displayName() == it
                        }
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.size(14.dp))

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (view) {

                LibraryView.COLLECTION -> {
                    CollectionView(
                        query = query,
                        order = libraryViewModel.order,
                        onCollectionClick = {
                            navigator.openSavedArticles(it)
                        })
                }

                LibraryView.HISTORY -> {
                    Text(
                        text = "History",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                LibraryView.RECENTLY_SAVED -> {
                    Text(
                        text = "Recently Saved",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                LibraryView.DOWNLOADED -> {
                    Text(
                        text = "Downloaded",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun LibraryScreenPreview() {
    val startKey = FeedScreenKey
    val backStack = rememberNavBackStack(startKey)
    val navigator = remember { Navigator(backStack) }
    LibraryScreen(navigator = navigator)
}