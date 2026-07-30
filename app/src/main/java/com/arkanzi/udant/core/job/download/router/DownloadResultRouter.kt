package com.arkanzi.udant.core.job.download.router

import com.arkanzi.udant.core.job.download.contract.DownloadPayload
import com.arkanzi.udant.core.job.download.model.DownloadType
import com.arkanzi.udant.feature.archive.model.ArchiveResultPayload
import com.arkanzi.udant.feature.archive.processor.ArchiveResultProcessor
import javax.inject.Inject

class DownloadResultRouter @Inject constructor(
    private val archiveProcessor: ArchiveResultProcessor,

    ) {

    suspend fun process(
        jobType: DownloadType,
        result: DownloadPayload
    ) {
        when (jobType) {
            DownloadType.ARCHIVE ->
                archiveProcessor.process(
                    result = result as ArchiveResultPayload
                )
        }
    }
}