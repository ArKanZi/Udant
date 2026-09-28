package com.arkanzi.udant.feature.library.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arkanzi.udant.core.model.ArchiveStatus
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.core.navigation.Navigator
import com.arkanzi.udant.core.ui.components.SearchBar
import com.arkanzi.udant.feature.library.model.LibraryCollectionTarget
import com.arkanzi.udant.feature.library.ui.components.shared_components.ArticleCard
import com.arkanzi.udant.feature.library.viewmodel.SavedArticleOrder
import com.arkanzi.udant.feature.library.viewmodel.SavedArticleSort
import com.arkanzi.udant.feature.library.viewmodel.SavedArticlesViewModel
import com.arkanzi.udant.feature.library.ui.components.shared_components.SortOption

@Composable
fun SavedArticleScreen(
    modifier: Modifier = Modifier,
    target: LibraryCollectionTarget,
    viewModel: SavedArticlesViewModel = hiltViewModel(),
    navigator: Navigator
) {
    LaunchedEffect(target) {
        viewModel.setCollectionTarget(target)
    }
    val articles by viewModel
        .articles
        .collectAsStateWithLifecycle()

    var query by remember {
        mutableStateOf("")
    }

    Column(
        modifier = modifier.fillMaxSize().padding(12.dp)
    ) {

        SearchBar(
            query = query,
            onQueryChange = { query = it },
            placeHolder = "Search your Articles...",
            onSearchClick = {},

        )

        Spacer(modifier = Modifier.size(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            SortOption(
                label = "Sort By",
                value = viewModel.sortBy.displayName(),
                options = SavedArticleSort.entries.map {
                    it.displayName()
                },
                onOptionSelected = { selected ->
                    viewModel.selectSortBy(
                        SavedArticleSort.entries.first {
                            it.displayName() == selected
                        }
                    )
                },
                modifier = Modifier.weight(1f)
            )

            SortOption(
                label = "Order By",
                value = viewModel.orderBy.displayName(),
                options = SavedArticleOrder.entries.map {
                    it.displayName()
                },
                onOptionSelected = { selected ->
                    viewModel.selectOrderBy(
                        SavedArticleOrder.entries.first {
                            it.displayName() == selected
                        }
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (articles.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Saved Articles"
                )
            }

        } else {

            SavedArticleList(
                articles = articles,
                viewModel = viewModel,
                navigator = navigator
            )
        }
    }
}

@Composable
private fun SavedArticleList(
    articles: List<Article>,
    viewModel: SavedArticlesViewModel,
    navigator: Navigator
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(
            bottom = 100.dp
        )
    ) {

        items(
            items = articles,
            key = { article ->
                article.articleId
            }
        ) { article ->

            ArticleCard(
                article = article,

                onClick = {
                    if (
                        article.archiveStatus == ArchiveStatus.COMPLETED &&
                        article.archiveUri != null
                    ) {
                        navigator.openWebView(
                            article.archiveUri
                        )
                    } else {
                        navigator.openWebView(
                            article.articleUrl
                        )
                    }
                },

                onMoreClick = {
                    // Article actions will go here.
                    // Remove / delete archive / etc.
                }
            )
        }
    }
}