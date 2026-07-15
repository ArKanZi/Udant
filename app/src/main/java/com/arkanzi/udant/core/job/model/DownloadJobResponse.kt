package com.arkanzi.udant.core.job.model

import kotlin.reflect.KClass

sealed interface DownloadJobResponse<out T> {

    val jobId: String
    val downloadJobType: DownloadJobType
    val timestamp:Long

    data class Success<T>(
        override val jobId: String,
        override val downloadJobType: DownloadJobType,
        override val timestamp: Long,
        val payload: T
    ) : DownloadJobResponse<T>

    data class Failure(
        override val jobId: String,
        override val downloadJobType: DownloadJobType,
        override val timestamp: Long,
        val header:String,
        val source: KClass<*>,
        val reason: DownloadJobFailureReason,
        val throwable: Throwable
    ) : DownloadJobResponse<Nothing>
}