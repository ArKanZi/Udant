package com.arkanzi.udant.core.job.download.processor

import com.arkanzi.udant.core.job.download.contract.DownloadPayload

interface DownloadResultProcessor<R : DownloadPayload> {
    suspend fun process(result: R)
}