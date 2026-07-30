package com.arkanzi.udant.feature.archive.model

data class ArchiveExecutionRequest(
    val action: ArchiveAction,
    val jobId: String,
    val articleUrl: String,
    val articleTitle: String
)