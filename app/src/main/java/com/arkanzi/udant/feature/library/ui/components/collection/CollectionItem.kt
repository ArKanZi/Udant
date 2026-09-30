package com.arkanzi.udant.feature.library.ui.components.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.arkanzi.udant.core.model.CollectionModel
import com.arkanzi.udant.core.ui.icons.folderIcon

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun CollectionItem(
    modifier: Modifier = Modifier,
    collection: CollectionModel,
    defaultCollectionId: String? = null,
    onClick: () -> Unit,
    onEditClick: (() -> Unit)? = null,
    onPinClick: (() -> Unit)? = null,
    onSetDefaultClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
) {
    val folderColor = Color(collection.color)
    val fillColor = folderColor.copy(alpha = 0.95f)

    val folderIconLocal = remember(fillColor, folderColor) {
        folderIcon(fillColor, folderColor)
    }

    var showMenu by remember {
        mutableStateOf(false)
    }

    Surface(
        modifier = modifier
            .dropShadow(
                shape = RoundedCornerShape(20.dp),
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
            .clip(RoundedCornerShape(20.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    showMenu = true
                }
            ),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Folder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(folderColor.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = folderIconLocal,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    tint = folderColor
                )
            }

            Spacer(Modifier.width(12.dp))

            // Name + article count
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = collection.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "${collection.articleCount} articles",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Trailing content
            if (defaultCollectionId==collection.id || (collection.id =="default" && defaultCollectionId == null) ) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Default",
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 5.dp
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        DropdownMenu(
            expanded = showMenu,
            shape = RoundedCornerShape(16.dp),
            onDismissRequest = {
                showMenu = false
            }
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = {
                    showMenu = false
                    onEditClick?.invoke()
                }
            )

            DropdownMenuItem(
                text = { Text(if (collection.isPinned) "Unpin" else "Pin") },
                onClick = {
                    showMenu = false
                    onPinClick?.invoke()
                }
            )

            if (collection.id != "default" || defaultCollectionId != null) {
                DropdownMenuItem(
                    text = {
                        Text(
                            if (
                                collection.id != "default" &&
                                defaultCollectionId == collection.id
                            ) {
                                "Remove as Default"
                            } else {
                                "Set as Default"
                            }
                        )
                    },
                    onClick = {
                        showMenu = false
                        onSetDefaultClick?.invoke()
                    }
                )
            }

            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    showMenu = false
                    onDeleteClick?.invoke()

                }
            )
        }
    }

}

@Preview
@Composable
fun CollectionItemPreview() {
    val now = System.currentTimeMillis()
    CollectionItem(
        collection = CollectionModel(
            id = "default",
            name = "Sample Collection",
            color = MaterialTheme.colorScheme.primary.toArgb().toLong(),
            isPinned = false,
            createdAt = now,
            articleCount = 20,
            updatedAt = now
        ),
        onClick = {}
    )
}