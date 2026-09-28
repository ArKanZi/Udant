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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.arkanzi.udant.core.ui.icons.folderIcon

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun CollectionItem(
    modifier: Modifier = Modifier,
    name: String,
    articleCount: Int,
    isDefault: Boolean = false,
    onClick: () -> Unit,
    onEditClick: (() -> Unit)? = null,
    onMoreClick: (() -> Unit)? = null
) {
    val folderColor = generateCollectionColor()
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
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "$articleCount articles",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Trailing content
            if (isDefault) {
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
            } else {
                IconButton(
                    onClick = { onMoreClick?.invoke() }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
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
                text = { Text("Pin") },
                onClick = {
                    showMenu = false
                    // pin
                }
            )

            DropdownMenuItem(
                text = { Text("Set as Default") },
                onClick = {
                    showMenu = false
                    // set default
                }
            )

            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    showMenu = false
                    // delete
                }
            )
        }
    }

}

@Preview
@Composable
fun CollectionItemPreview() {
    CollectionItem(
        name = "Sample Collection",
        articleCount = 10,
        isDefault = true,
        onClick = {},
        onMoreClick = {}
    )
}