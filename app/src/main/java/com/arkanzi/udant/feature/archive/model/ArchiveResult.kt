package com.arkanzi.udant.feature.archive.model

import kotlin.reflect.KClass

sealed interface ArchiveResult {
    val jobId: String
    val savedArticleId : Long?
    val timestamp:Long

    data class Success(
        override val jobId: String,
        override val savedArticleId : Long? = null,
        override val timestamp: Long,
        val uri: String? = null
    ) : ArchiveResult

    data class Failure(
        override val jobId: String,
        override val savedArticleId : Long? = null,
        override val timestamp: Long,
        val header:String,
        val source: KClass<*>,
        val reason: ArchiveFailureReason,
        val throwable: Throwable
    ) : ArchiveResult

    data class Paused(
        override val jobId: String,
        override val savedArticleId: Long?= null,
        override val timestamp: Long
        ): ArchiveResult
}