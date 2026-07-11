package com.arkanzi.udant.core.job.handler

import com.arkanzi.udant.core.database.entity.DownloadJobEntity
import com.arkanzi.udant.core.job.model.DownloadJobResponse
import com.arkanzi.udant.core.job.model.DownloadPayload

interface DownloadJobHandler<T: DownloadPayload> {

    suspend fun execute(
        job: DownloadJobEntity
    ): DownloadJobResponse<T>

}