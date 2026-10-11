package com.arkanzi.udant.feature.library.ui.components.shared_components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.arkanzi.udant.R
import com.arkanzi.udant.core.model.ArchiveStatus
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.core.util.formatRelativeTimeShort

@Composable
fun ArticleCard(
    modifier: Modifier = Modifier,
    article: Article,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onMoveClick: (() -> Unit),
    onPinClick: (() -> Unit),
    onDeleteClick: (() -> Unit),
    onArchiveDeleteClick:() -> Unit
) {
    val cardShape = RoundedCornerShape(18.dp)
    var showMenu by remember {
        mutableStateOf(false)
    }
    var showDownloadOptions by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = modifier
            .dropShadow(
                shape = cardShape,
                shadow = Shadow(
                    radius = 8.dp,
                    spread = 0.dp,
                    color = Color.Black.copy(alpha = 0.06f),
                    offset = DpOffset(
                        x = 0.dp,
                        y = 2.dp
                    )
                )
            )
            .fillMaxWidth()
            .clip(cardShape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showMenu = true }
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.Top
        ) {

            AsyncImage(
                model = article.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(cardShape)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentScale = ContentScale.Crop
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(6.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {

                    Text(
                        text = article.title,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        minLines = 2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

//                    IconButton(
//                        onClick = onMoreClick,
//                        modifier = Modifier.size(24.dp)
//                    ) {
//                        Icon(
//                            imageVector = Icons.Outlined.MoreVert,
//                            contentDescription = "More options",
//                            modifier = Modifier.size(20.dp)
//                        )
//                    }
                }

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = article.sourceName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )

                    Text(
                        text = formatRelativeTimeShort(article.savedAt ?: 0L),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            if (article.archiveStatus != ArchiveStatus.COMPLETED) {
                                onDownloadClick()
                            } else {
                                 showDownloadOptions = true
                            }},
                        modifier = Modifier.size(20.dp)
                    ) {
                        if (article.archiveStatus == ArchiveStatus.COMPLETED) {

                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_check),
                                    contentDescription = "Downloaded",
                                    modifier = Modifier.size(15.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )

                            }
                            DropdownMenu(
                                expanded = showDownloadOptions,
                                onDismissRequest = { showDownloadOptions = false } // Closes when clicking outside
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Delete") },
                                    onClick = {
                                        showDownloadOptions = false
                                        onArchiveDeleteClick()

                                    }
                                )
                            }

                        } else {

                            Icon(
                                painter = painterResource(R.drawable.ic_download),
                                contentDescription = "Download",
                                modifier = Modifier.size(21.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
    DropdownMenu(
        expanded = showMenu,
        shape = RoundedCornerShape(16.dp),
        onDismissRequest = {
            showMenu = false
        }
    ) {
        DropdownMenuItem(
            text = { Text("Manage") },
            onClick = {
                showMenu = false
                onMoveClick()
            }
        )

        DropdownMenuItem(
            text = { Text(if (false) "Unpin" else "Pin") },
            onClick = {
                showMenu = false
                onPinClick()
            }
        )

        DropdownMenuItem(
            text = { Text("Delete") },
            onClick = {
                showMenu = false
                onDeleteClick()

            }
        )
    }
}

@Preview
@Composable
private fun ArticleCardPreview() {
    ArticleCard(
        article = Article(
            articleId = "",
            title = "Something is happening zkljdshfg alkwdfgh alkwdf",
            summary = "lol",
            author = "Random Person",
            imageUrl = "",
            sourceId = "",
            sourceName = "t skejh lskgfh ",
            articleUrl = "",
            publishedAt = 0,
            category = "",
            savedAt = 0,
            archiveStatus = ArchiveStatus.NOT_ARCHIVED,
            archiveUri = null
        ),
        onClick = {},
        onDownloadClick = {},
        onMoveClick = {},
        onPinClick = {},
        onDeleteClick = {},
        onArchiveDeleteClick = {}
    )
}