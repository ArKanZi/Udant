package com.arkanzi.udant.feature.archive.usecase

import androidx.core.net.toUri
import com.arkanzi.udant.core.storage.StorageManager
import com.arkanzi.udant.feature.archive.repository.ArchiveRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class DeleteArchiveUseCase @Inject constructor(
    private val archiveRepository: ArchiveRepository,
    private val storageManager: StorageManager
) {
    suspend operator fun invoke(savedArticleId: Long) {
        val archiveFolderUri =
            archiveRepository
                .getArchiveFolderUri()
                .firstOrNull()

        val article =
            archiveRepository
                .getSavedArticleById(
                    savedArticleId
                ) ?: return

        val archiveUri =
            article.archiveUri
                ?: return


        if (storageManager
                .deleteFileInSaf(
                    archiveFolderUri?.toUri(),
                    archiveUri.toUri()
                )
        ) {
            archiveRepository.deleteArchive(
                savedArticleId
            )
        }

    }
}