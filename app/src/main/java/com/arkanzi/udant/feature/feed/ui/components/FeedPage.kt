package com.arkanzi.udant.feature.feed.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.arkanzi.udant.R
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.core.model.FeedCategory
import com.arkanzi.udant.core.ui.theme.UdantTheme
import com.arkanzi.udant.core.util.formatRelativeTime
@Composable
fun FeedPage(
    article: Article,
    modifier: Modifier= Modifier,
    isSaved: Boolean,
    onArticleClick: (String) -> Unit,
    onSaveClick: () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .aspectRatio(1.37f)
                .clip(RoundedCornerShape(16.dp))
        ) {
            article.imageUrl?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = article.title,
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize(),

                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_fav_icon_toi),
                            contentDescription = "sourceIcon",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = article.sourceName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Row {
                        Icon(
                            painter = if (isSaved) {
                                painterResource(R.drawable.ic_bookmark_filled)
                            } else {
                                painterResource(R.drawable.ic_bookmark_outline)
                            },

                            contentDescription = if (isSaved) {
                                "Remove Saved Article"
                            } else {
                                "Save Article"
                            },

                            tint = MaterialTheme.colorScheme.outline,

                            modifier = Modifier
                                .clickable(
                                    onClick = onSaveClick
                                )
                                .size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        Icon(
                            painter = painterResource(R.drawable.ic_share_outline),

                            contentDescription = "Share Article",

                            tint = MaterialTheme.colorScheme.outline,

                            modifier = Modifier
                                .clickable(
                                    onClick = {}
                                )
                                .size(20.dp)
                        )
                    }

                }


                Text(
                    modifier = Modifier.clickable(onClick = { onArticleClick(article.articleUrl) }),
                    text = article.title,
                    style = MaterialTheme.typography.titleLargeEmphasized,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 9,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = buildString {
                        append(formatRelativeTime(article.publishedAt))

                        article.author
                            ?.takeIf { it.isNotBlank() }
                            ?.let {
                                append(" | By $it")
                            }
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Text(
                text = "Swipe for next",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
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
                author = "John DOE",
                category = "World",
                savedAt = 0
            ),
            onArticleClick = {},
            isSaved = true,
            onSaveClick = {}
        )
    }
}