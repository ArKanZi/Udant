package com.arkanzi.udant.core.job.model

sealed interface DownloadJobResponse<out T> {

    val jobId: String

    data class Success<T>(
        override val jobId: String,
        val payload: T
    ) : DownloadJobResponse<T>

    data class Failure(
        override val jobId: String,
        val throwable: Throwable
    ) : DownloadJobResponse<Nothing>
}