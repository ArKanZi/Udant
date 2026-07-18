package com.arkanzi.udant.core.job.download.model

import com.arkanzi.udant.core.job.download.contract.DownloadPayload

sealed interface DownloadRequest<T : DownloadPayload> {

    val downloadType: DownloadType
    val referenceId: Long
    val payload: T

    data class Execute<T : DownloadPayload>(
        override val downloadType: DownloadType,
        override val referenceId: Long,
        override val payload: T
    ) : DownloadRequest<T>
}