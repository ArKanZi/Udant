package com.arkanzi.udant.core.job.download.model

data class DownloadUiState(
    val jobId: String,
    val title: String,
    val status: DownloadStatus,
    val progress: Int
)