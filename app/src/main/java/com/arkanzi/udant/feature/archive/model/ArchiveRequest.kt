package com.arkanzi.udant.feature.archive.model

data class ArchiveRequest(
    val savedArticleId: Long,
    val articleTitle: String,
    val articleUrl: String
)