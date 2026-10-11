package com.arkanzi.udant.feature.archive.model

data class ArchiveRequest(
    val savedArticleId: String,
    val articleTitle: String,
    val articleUrl: String
)