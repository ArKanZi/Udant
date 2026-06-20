package com.arkanzi.udant.feature.feed.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.core.navigation.Navigator
import com.arkanzi.udant.core.ui.theme.UdantTheme
import com.arkanzi.udant.feature.feed.viewmodel.FeedViewModel
import kotlinx.coroutines.launch

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel(),
    navigator: Navigator
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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

    Box(
        modifier = modifier.fillMaxSize()
    ) {

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

                VerticalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->

                    val article = uiState.articles[page]

                    FeedPage(
                        article = article,
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

        if (
            uiState.isLoading &&
            uiState.articles.isNotEmpty()
        ) {

            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),

                strokeWidth = 2.dp,

                color = Color.White
            )
        }

        FloatingActionButton(

            onClick = {

                viewModel.refreshFeed()
                scope.launch {
                    pagerState.scrollToPage(0)
                }

            },

            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),

            containerColor = Color(0xFF1A1A1A)

        ) {

            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = "Refresh Feed",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun FeedPage(
    article: Article,
    isSaved: Boolean,
    onArticleClick: (String) -> Unit,
    onSaveClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        article.imageUrl?.let { imageUrl ->

            AsyncImage(
                model = imageUrl,
                contentDescription = article.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f),

                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),

                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row {
                    Text(
                        text = article.sourceName,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.LightGray
                    )
                    if (article.category != "Default") {
                        Text(
                            text = " - " + article.category,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.LightGray
                        )
                    }
                    Spacer(modifier = Modifier.size(5.dp))
                    Icon(
                        imageVector = if (isSaved) {
                            Icons.Filled.Bookmark
                        } else {
                            Icons.Outlined.BookmarkBorder
                        },

                        contentDescription = if (isSaved) {
                            "Remove Saved Article"
                        } else {
                            "Save Article"
                        },

                        tint = if (isSaved) {
                            Color.White
                        } else {
                            Color.LightGray
                        },

                        modifier = Modifier.clickable(
                            onClick = onSaveClick
                        )
                    )
                }


                Text(
                    modifier = Modifier.clickable(onClick = { onArticleClick(article.articleUrl) }),
                    text = article.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = article.author ?: "Unknown",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            }

            Text(
                text = "Swipe for next",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedScreenPreview() {

    UdantTheme {

        FeedPage(
            article = Article(
                articleId = 1,
                title = "Sample Article Title That Might Be Long and Need Several Lines to Display Correct",
                summary = "This is a sample summary for the article. It provides a brief overview of what the article is about and should be long enough to test the max lines property of the text component.",
                imageUrl = "https://example.com/image.jpg",
                articleUrl = "https://example.com/article",
                publishedAt = System.currentTimeMillis(),
                sourceName = "Tech News",
                author = "John Doe",
                category = "World",
                savedAt = 0
            ),
            onArticleClick = {},
            isSaved = true,
            onSaveClick = {}
        )
    }
}