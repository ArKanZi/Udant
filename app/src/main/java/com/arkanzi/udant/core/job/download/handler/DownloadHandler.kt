package com.arkanzi.udant.core.job.download.handler

import com.arkanzi.udant.core.database.entity.DownloadJobEntity
import com.arkanzi.udant.core.job.download.model.DownloadResponse
import com.arkanzi.udant.core.job.download.contract.DownloadPayload

interface DownloadHandler<T: DownloadPayload> {

    suspend fun execute(
        job: DownloadJobEntity
    ): DownloadResponse<T>

}