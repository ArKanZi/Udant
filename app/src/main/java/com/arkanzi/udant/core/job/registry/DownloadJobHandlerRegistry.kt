package com.arkanzi.udant.core.job.registry

import com.arkanzi.udant.core.job.handler.DownloadJobHandler
import com.arkanzi.udant.core.job.model.DownloadJobType
import com.arkanzi.udant.feature.archive.handler.ArchiveJobHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadJobHandlerRegistry @Inject constructor(

    private val archiveHandler: ArchiveJobHandler

) {

    fun get(
        type: DownloadJobType
    ): DownloadJobHandler<*> {
        return when (type) {
            DownloadJobType.ARCHIVE -> archiveHandler
        }
    }
}