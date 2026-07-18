package com.arkanzi.udant.core.job.download.model

import com.arkanzi.udant.core.job.download.contract.DownloadFailureReason
import kotlin.reflect.KClass

sealed interface DownloadResponse<out T> {

    val jobId: String
    val downloadType: DownloadType
    val timestamp:Long

    data class Success<T>(
        override val jobId: String,
        override val downloadType: DownloadType,
        override val timestamp: Long,
        val payload: T
    ) : DownloadResponse<T>

    data class Failure(
        override val jobId: String,
        override val downloadType: DownloadType,
        override val timestamp: Long,
        val header:String,
        val source: KClass<*>,
        val reason: DownloadFailureReason,
        val throwable: Throwable
    ) : DownloadResponse<Nothing>
}