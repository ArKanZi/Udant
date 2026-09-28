package com.arkanzi.udant.feature.library.ui.components.collection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun CollectionDialog(
    title: String,
    initialName: String = "",
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    onResetNameCheck: () -> Unit,
    collectionNameExists: Boolean,
    onNameChanged: (String) -> Unit
) {
    var name by remember(initialName) {
        mutableStateOf(initialName)
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(title)
        },

        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        onNameChanged(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = collectionNameExists,
                    supportingText = {
                        if (collectionNameExists) {
                            Text("Collection already exists")
                        }
                    },
                    label = {
                        Text("Collection name")
                    },
                    placeholder = {
                        Text("e.g. Android")
                    }
                )
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    onResetNameCheck()
                    onConfirm(name.trim())
                },
                enabled = name.isNotBlank() && !collectionNameExists
            ) {
                Text(confirmText)
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