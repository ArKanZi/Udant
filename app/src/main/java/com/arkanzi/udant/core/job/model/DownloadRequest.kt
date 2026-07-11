package com.arkanzi.udant.core.job.model

sealed interface DownloadRequest<T : DownloadPayload> {

    val downloadJobType: DownloadJobType
    val referenceId: Long
    val payload: T

    data class Execute<T : DownloadPayload>(
        override val downloadJobType: DownloadJobType,
        override val referenceId: Long,
        override val payload: T
    ) : DownloadRequest<T>
}