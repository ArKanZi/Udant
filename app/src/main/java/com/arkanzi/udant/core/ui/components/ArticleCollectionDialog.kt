package com.arkanzi.udant.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkanzi.udant.core.model.CollectionModel

@Composable
fun ArticleCollectionDialog(
    collections: List<CollectionModel>,
    selectedCollectionIds: Set<String>,
    isUncategorized: Boolean,
    onCollectionToggle: (String) -> Unit,
    onUncategorizedToggle: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = "Save to"
            )
        },

        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                // Uncategorized
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isUncategorized,
                        onCheckedChange = {
                            onUncategorizedToggle()
                        }
                    )

                    Text(
                        text = "Uncategorized"
                    )
                }

                if(collections.isNotEmpty()){
                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "Collections"
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    collections.forEach { collection ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = collection.id in selectedCollectionIds,
                                onCheckedChange = {
                                    onCollectionToggle(collection.id)
                                }
                            )

                            Text(
                                text = collection.name
                            )
                        }
                    }
                }
            }
        },

        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("Done")
            }
        },

        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}