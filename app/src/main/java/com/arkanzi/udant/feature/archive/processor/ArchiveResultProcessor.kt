package com.arkanzi.udant.feature.archive.processor

import com.arkanzi.udant.core.database.entity.DownloadJobEntity
import com.arkanzi.udant.core.job.download.processor.DownloadResultProcessor
import com.arkanzi.udant.core.model.ArchiveStatus
import com.arkanzi.udant.feature.archive.model.ArchiveResultPayload
import com.arkanzi.udant.feature.savedArticles.repository.SavedArticleRepository
import javax.inject.Inject

class ArchiveResultProcessor @Inject constructor(
    private val savedArticleRepository: SavedArticleRepository
) : DownloadResultProcessor<ArchiveResultPayload> {

    override suspend fun process(
        result: ArchiveResultPayload
    ) {
        savedArticleRepository.updateArchive(
            savedArticleId = result.savedArticleId,
            archiveUri = result.archiveUri,
            archiveStatus = ArchiveStatus.COMPLETED
        )
    }

}