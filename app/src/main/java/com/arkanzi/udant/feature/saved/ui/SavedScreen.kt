package com.arkanzi.udant.feature.saved.ui

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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.core.navigation.Navigator
import com.arkanzi.udant.feature.saved.viewmodel.SavedArticleViewModel

@Composable
fun SavedScreen(

    modifier: Modifier = Modifier,

    viewModel: SavedArticleViewModel = hiltViewModel(),

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
            key = { article -> article.articleUrl }
        ) { article ->

            SavedArticleItem(

                article = article,

                onArticleClick = {

                    navigator.openWebView(
                        article.articleUrl
                    )
                },

                onRemoveClick = {

                    viewModel.removeSavedArticle(
                        article.articleUrl
                    )
                }
            )
        }
    }
}

@Composable
private fun SavedArticleItem(

    article: Article,

    onArticleClick: () -> Unit,

    onRemoveClick: () -> Unit

) {

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
