package com.arkanzi.udant.feature.feed.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arkanzi.udant.core.navigation.Navigator
import com.arkanzi.udant.feature.feed.ui.components.CategoryBar
import com.arkanzi.udant.feature.feed.ui.components.FeedPage
import com.arkanzi.udant.feature.feed.viewmodel.FeedViewModel
import kotlinx.coroutines.launch

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel(),
    navigator: Navigator
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(
        pageCount = { uiState.articles.size }
    )
    var lastRequestedPage by remember {
        mutableIntStateOf(-1)
    }

    val savedUrls by viewModel
        .savedUrls
        .collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()

    LaunchedEffect(
        pagerState.currentPage,
        uiState.articles.size
    ) {

        val shouldFetchMore =
            pagerState.currentPage >=
                    uiState.articles.lastIndex - 5

        if (
            shouldFetchMore &&
            lastRequestedPage != pagerState.currentPage
        ) {

            lastRequestedPage = pagerState.currentPage

            viewModel.fetchNextFeed()
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        if(uiState.categories.size !=1){
            CategoryBar(
                categories = uiState.categories.map { it.name },
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    viewModel.selectCategory(category)
                    scope.launch {
                        pagerState.scrollToPage(0)
                    }}
            )
        }
        when {

            uiState.isLoading &&
                    uiState.articles.isEmpty() -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator(
                        color = Color.White
                    )
                }
            }

            uiState.error != null &&
                    uiState.articles.isEmpty() -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = uiState.error ?: "Unknown Error",
                        color = Color.Red
                    )
                }
            }

            else -> {
                PullToRefreshBox(
                    isRefreshing = uiState.isLoading,
                    onRefresh = {
                        viewModel.refreshFeed()
                        scope.launch {
                            pagerState.scrollToPage(0)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) {

                    VerticalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->

                        val article = uiState.articles[page]

                        FeedPage(

                            article = article,
                            modifier = Modifier.padding(bottom = 78.dp),
                            isSaved = article.articleUrl in savedUrls,
                            onArticleClick = navigator::openWebView,
                            onSaveClick = {

                                if (article.articleUrl in savedUrls) {

                                    viewModel.removeSavedArticle(
                                        article.articleUrl
                                    )

                                } else {

                                    viewModel.saveArticle(article)
                                }
                            }
                        )
                    }
                }
            }
        }

        if (
            uiState.isLoading &&
            uiState.articles.isNotEmpty()
        ) {

            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp),

                strokeWidth = 2.dp,

                color = Color.White
            )
        }
    }
}