package com.arkanzi.udant.feature.settings.ui

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.arkanzi.udant.feature.settings.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    val preferences by
    viewModel.preferences.collectAsState()

    val isArchiveReady =
        preferences.archiveFolderUri
            ?.let { uri ->

                viewModel.validateArchiveFolder(
                    context,
                    uri
                )
            } ?: false

    val darkMode = remember {
        mutableStateOf(true)
    }

    val imageLoading = remember {
        mutableStateOf(true)
    }

    val feedStyle = remember {
        mutableStateOf("Inshorts")
    }

    val folderPickerLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.OpenDocumentTree()
        ) { uri ->

            if (uri == null) {
                return@rememberLauncherForActivityResult
            }

            context.contentResolver
                .takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

            viewModel.saveArchiveFolderUri(
                uri.toString()
            )

            Toast.makeText(
                context,
                "Folder selected successfully",
                Toast.LENGTH_SHORT
            ).show()

            val folder =
                DocumentFile.fromTreeUri(
                    context,
                    uri
                )

            val file =
                folder?.createFile(
                    "text/plain",
                    "udant_test.txt"
                )

            file?.uri?.let { fileUri ->

                context.contentResolver
                    .openOutputStream(fileUri)
                    ?.use { stream ->

                        stream.write(
                            "Udant SAF Test File"
                                .toByteArray()
                        )
                    }

                Toast.makeText(
                    context,
                    "Test file created successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement =
            Arrangement.spacedBy(20.dp)
    ) {

        Text(
            text = "Settings",
            style =
                MaterialTheme.typography
                    .headlineMedium,
            color = Color.White
        )

        HorizontalDivider()

        Column(
            modifier =
                Modifier.fillMaxWidth(),
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "Dark Mode",
                style =
                    MaterialTheme.typography
                        .titleMedium,
                color = Color.White
            )

            Switch(
                checked = darkMode.value,
                onCheckedChange = {
                    darkMode.value = it
                }
            )
        }

        Column(
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "Feed Style",
                style =
                    MaterialTheme.typography
                        .titleMedium,
                color = Color.White
            )

            Button(
                onClick = {

                    feedStyle.value =
                        if (
                            feedStyle.value ==
                            "Inshorts"
                        ) {
                            "Classic"
                        } else {
                            "Inshorts"
                        }
                }
            ) {

                Text(
                    text = feedStyle.value
                )
            }
        }

        Column(
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "Image Loading",
                style =
                    MaterialTheme.typography
                        .titleMedium,
                color = Color.White
            )

            Switch(
                checked = imageLoading.value,
                onCheckedChange = {
                    imageLoading.value = it
                }
            )
        }

        HorizontalDivider()

        Column(
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "Archive",
                style =
                    MaterialTheme.typography
                        .titleMedium,
                color = Color.White
            )

            Text(
                text =
                    if (isArchiveReady) {
                        "✓ Archive folder ready"
                    } else {
                        "Archive folder not configured"
                    },
                color = Color.White
            )

            Button(
                onClick = {
                    folderPickerLauncher.launch(
                        null
                    )
                }
            ) {

                Text(
                    text =
                        if (isArchiveReady) {
                            "Change Archive Folder"
                        } else {
                            "Select Archive Folder"
                        }
                )
            }
            Text(
                text = preferences.archiveFolderUri ?: "NULL",
                color = Color.White
            )
        }
    }
}