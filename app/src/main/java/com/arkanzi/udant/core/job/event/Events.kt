package com.arkanzi.udant.core.job.event

import com.arkanzi.udant.core.job.model.DownloadJobType
import com.arkanzi.udant.core.job.model.DownloadPayload

sealed interface Events {

    data class Completed(
        val jobId: String,
        val jobType: DownloadJobType,
        val payload: DownloadPayload
    ) : Events

    data class Failed(
        val jobId: String,
        val jobType: DownloadJobType,
        val throwable: Throwable
    ) : Events
}