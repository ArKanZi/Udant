package com.arkanzi.udant.core.job.download.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arkanzi.udant.core.database.entity.DownloadJobEntity
import com.arkanzi.udant.core.job.download.model.DownloadStatus
import com.arkanzi.udant.core.job.download.viewmodel.DownloadViewModel

@Composable
fun DownloadScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: DownloadViewModel = hiltViewModel()
    val progress by viewModel.loadingProgress.collectAsStateWithLifecycle()
    val isRunning by viewModel.isRunning.collectAsStateWithLifecycle()


    val downloads by viewModel
        .downloads
        .collectAsStateWithLifecycle()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(

            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)

        ) {

            items(
                items = downloads,
                key = { it.jobId }
            ) { job ->

                DownloadItem(
                    job = job,
                    progress = progress,
                    onDelete = {},
                    onPause = {},
                    onStart = {}
                )
            }

        }
        FloatingActionButton(
            onClick = {
                if (isRunning) {
                    viewModel.pause()
                } else {
                    viewModel.start()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = if (isRunning) {
                    Icons.Default.Pause
                } else {
                    Icons.Default.PlayArrow
                },
                contentDescription = if (isRunning) {
                    "Pause downloads"
                } else {
                    "Start downloads"
                }
            )
        }
    }
}

@Composable
private fun DownloadItem(

    job: DownloadJobEntity,

    progress: Int,

    onDelete: () -> Unit,

    onPause: () -> Unit,

    onStart: () -> Unit

) {

    var expanded by remember { mutableStateOf(false) }

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),

        verticalAlignment = Alignment.CenterVertically

    ) {

        Icon(
            imageVector = Icons.Default.Archive,
            contentDescription = null,
            tint = Color.White
        )

        Spacer(
            modifier = Modifier.size(16.dp)
        )

        Text(

            text = job.title,

            modifier = Modifier.weight(1f),

            color = Color.White,

            maxLines = 1,

            overflow = TextOverflow.Ellipsis

        )

        Spacer(
            modifier = Modifier.size(12.dp)
        )

        when (job.status) {

            DownloadStatus.QUEUED -> {

                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 2.dp
                )
            }

            DownloadStatus.RUNNING -> {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier.size(36.dp),
                        strokeWidth = 2.dp
                    )

                    Text(
                        text = "$progress%",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }

            DownloadStatus.RUNNING,
            DownloadStatus.COPYING,
            DownloadStatus.CLEANING -> {

                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 2.dp
                )
            }

            DownloadStatus.COMPLETED -> {

                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.Green
                )
            }

            DownloadStatus.FAILED -> {

                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = Color.Red
                )
            }

            DownloadStatus.PAUSED -> {

                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = null,
                    tint = Color.Yellow
                )
            }

            else -> {}
        }

        Box {

            IconButton(
                onClick = {
                    expanded = true
                }
            ) {

                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color.White
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {

                DropdownMenuItem(

                    text = {
                        Text("Start")
                    },

                    leadingIcon = {

                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                    },

                    onClick = {

                        expanded = false
                        onStart()
                    }

                )

                DropdownMenuItem(

                    text = {
                        Text("Pause")
                    },

                    leadingIcon = {

                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = null
                        )
                    },

                    onClick = {

                        expanded = false
                        onPause()
                    }

                )

                DropdownMenuItem(

                    text = {
                        Text("Delete")
                    },

                    leadingIcon = {

                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                    },

                    onClick = {

                        expanded = false
                        onDelete()
                    }

                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DownloadScreenPreview() {

    DownloadScreen()

}