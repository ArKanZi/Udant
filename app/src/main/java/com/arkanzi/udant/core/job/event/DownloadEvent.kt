package com.arkanzi.udant.core.job.event

import com.arkanzi.udant.core.job.model.DownloadJobType
import com.arkanzi.udant.core.job.model.DownloadPayload

sealed interface DownloadEvent {

    data class Completed(
        val jobId: String,
        val jobType: DownloadJobType,
        val payload: DownloadPayload
    ) : DownloadEvent

    data class Failed(
        val jobId: String,
        val jobType: DownloadJobType,
        val throwable: Throwable
    ) : DownloadEvent
}