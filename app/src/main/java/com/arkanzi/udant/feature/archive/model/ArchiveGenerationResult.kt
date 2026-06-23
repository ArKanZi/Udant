package com.arkanzi.udant.feature.archive.model

import java.io.File

sealed interface ArchiveServiceResult {
    val savedArticleId: Long

    data class Success(
        override val savedArticleId: Long,
        val fileName: String,
        val localFile: File
    ) : ArchiveServiceResult

    data class Failure(
        override val savedArticleId: Long,
        val throwable: Throwable
    ) : ArchiveServiceResult
}