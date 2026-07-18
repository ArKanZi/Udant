package com.arkanzi.udant.core.job.download.registry

import com.arkanzi.udant.core.job.download.handler.DownloadHandler
import com.arkanzi.udant.core.job.download.model.DownloadType
import com.arkanzi.udant.feature.archive.handler.ArchiveHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadHandlerRegistry @Inject constructor(

    private val archiveHandler: ArchiveHandler

) {

    fun get(
        type: DownloadType
    ): DownloadHandler<*> {
        return when (type) {
            DownloadType.ARCHIVE -> archiveHandler
        }
    }
}