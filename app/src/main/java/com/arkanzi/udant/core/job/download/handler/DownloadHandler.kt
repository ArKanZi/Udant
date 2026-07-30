package com.arkanzi.udant.core.job.download.handler

import com.arkanzi.udant.core.database.entity.DownloadJobEntity
import com.arkanzi.udant.core.job.download.model.DownloadResult
import com.arkanzi.udant.core.job.download.contract.DownloadPayload
import com.arkanzi.udant.core.job.download.model.DownloadAction

interface DownloadHandler<T: DownloadPayload> {

    suspend fun execute(
        action: DownloadAction,
        job: DownloadJobEntity
    ): DownloadResult<T>

}