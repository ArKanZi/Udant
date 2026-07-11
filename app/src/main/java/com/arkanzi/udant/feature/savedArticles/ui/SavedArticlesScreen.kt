package com.arkanzi.udant.feature.savedArticles.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arkanzi.udant.core.model.ArchiveStatus
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.core.navigation.Navigator
import com.arkanzi.udant.feature.archive.model.ArchiveRequest
import com.arkanzi.udant.feature.savedArticles.viewmodel.SavedArticlesViewModel

@Composable
fun SavedArticlesScreen(

    modifier: Modifier = Modifier,

    viewModel: SavedArticlesViewModel = hiltViewModel(),

    navigator: Navigator

) {
    val articles by viewModel
        .articles
        .collectAsStateWithLifecycle()
    if (articles.isEmpty()) {

        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "No Saved Articles",
                color = Color.White
            )
        }

        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        items(
            items = articles,
            key = { article -> article.articleId }
        ) { article ->

            SavedArticleItem(

                article = article,

                onArticleClick = {
                    if (article.archiveStatus == ArchiveStatus.COMPLETED && article.archiveUri != null) {
                        navigator.openWebView(
                            article.archiveUri
                        )
                    } else {
                        navigator.openWebView(
                            article.articleUrl
                        )
                    }

                },

                onRemoveClick = {

                    viewModel.removeSavedArticle(
                        article.articleUrl
                    )
                },
                onArchiveClick = {
//                    viewModel.archive(articleId = article.articleId)
                    viewModel.archiveSavedArticle(
                        ArchiveRequest(
                            savedArticleId = article.articleId,
                            articleTitle = article.title,
                            articleUrl = article.articleUrl
                        )
                    )

                },
                onArchiveDeleteClick = {
                    viewModel.deleteArchive(articleId = article.articleId)
                }
            )
        }
    }
}

@Composable
private fun SavedArticleItem(

    article: Article,

    onArticleClick: () -> Unit,

    onRemoveClick: () -> Unit,

    onArchiveClick: () -> Unit,

    onArchiveDeleteClick: () -> Unit

) {

    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onArticleClick
            )
            .padding(16.dp),

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = article.title,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = article.sourceName,
                color = Color.Gray
            )
        }
        Box {
            Icon(
                imageVector = if (article.archiveStatus == ArchiveStatus.COMPLETED) {
                    Icons.Filled.Archive
                } else {
                    Icons.Outlined.Archive
                },

                contentDescription = if (article.archiveStatus == ArchiveStatus.COMPLETED) {
                    "Remove Saved Article"
                } else {
                    "Save Article"
                },

                tint = if (article.archiveStatus == ArchiveStatus.COMPLETED) {
                    Color.White
                } else {
                    Color.LightGray
                },
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .clickable(
                        onClick = if (article.archiveStatus != ArchiveStatus.COMPLETED) {
                            onArchiveClick
                        } else {
                            { isExpanded = true }
                        }
                    )
            )
            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false } // Closes when clicking outside
            ) {
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        isExpanded = false
                        onArchiveDeleteClick()

                    }
                )
            }
        }



        Icon(
            imageVector = Icons.Filled.Bookmark,
            contentDescription = "Remove Saved Article",
            tint = Color.White,
            modifier = Modifier.clickable(
                onClick = onRemoveClick
            )
        )
    }
}

@Preview
@Composable
fun SavedArticleItemPreview() {
    SavedArticleItem(
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
        onRemoveClick = {},
        onArchiveClick = {},
        onArchiveDeleteClick = {}
    )
}