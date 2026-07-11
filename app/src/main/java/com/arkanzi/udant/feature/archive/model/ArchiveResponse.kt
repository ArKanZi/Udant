package com.arkanzi.udant.feature.archive.model

sealed interface ArchiveResponse {
    val jobId: String
    val savedArticleId : Long?

    data class Success(
        override val jobId: String,
        override val savedArticleId : Long? = null,
        val uri: String? = null
    ) : ArchiveResponse

    data class Failure(
        override val jobId: String,
        override val savedArticleId : Long? = null,
        val throwable: Throwable
    ) : ArchiveResponse
}