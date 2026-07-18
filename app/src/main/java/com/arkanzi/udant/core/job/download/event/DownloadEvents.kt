package com.arkanzi.udant.core.job.download.event

import com.arkanzi.udant.core.job.download.model.DownloadType
import com.arkanzi.udant.core.job.download.contract.DownloadPayload

sealed interface DownloadEvents {

    data class Completed(
        val jobId: String,
        val jobType: DownloadType,
        val payload: DownloadPayload
    ) : DownloadEvents

    data class Failed(
        val jobId: String,
        val jobType: DownloadType,
        val throwable: Throwable
    ) : DownloadEvents
}