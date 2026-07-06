package com.arkanzi.udant.feature.archive.model

data class ArchiveJobRequest(
    val articleId: Long,
    val articleTitle: String,
    val articleUrl: String
)