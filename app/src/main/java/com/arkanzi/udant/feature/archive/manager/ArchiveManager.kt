package com.arkanzi.udant.feature.archive.manager

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.arkanzi.udant.core.model.ArchiveStatus
import com.arkanzi.udant.core.notification.NotificationController
import com.arkanzi.udant.core.storage.StorageManager
import com.arkanzi.udant.feature.archive.repository.ArchiveRepository
import com.arkanzi.udant.feature.archive.service.ArchiveService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveManager @Inject constructor(

    @ApplicationContext
    private val context: Context,

    private val archiveRepository: ArchiveRepository,

    private val notificationController: NotificationController,

    private val storageManager: StorageManager

) {

    suspend fun archive(
        savedArticleId: Long
    ): Result<Unit> {

        val article =
            archiveRepository
                .getSavedArticleById(
                    savedArticleId
                )
                ?: return Result.failure(
                    IllegalStateException(
                        "Article not found"
                    )
                )

        val archiveFolderUri =
            archiveRepository
                .getArchiveFolderUri()
                .firstOrNull()

        if (archiveFolderUri == null) {

            return Result.failure(
                IllegalStateException(
                    "Archive folder not configured"
                )
            )
        }

        when (
            article.archiveStatus
        ) {

            ArchiveStatus.QUEUED,
            ArchiveStatus.ARCHIVING -> {

                return Result.failure(
                    IllegalStateException(
                        "Archive already in progress"
                    )
                )
            }

            ArchiveStatus.COMPLETED -> {

                return Result.failure(
                    IllegalStateException(
                        "Article already archived"
                    )
                )
            }

            else -> Unit
        }

        archiveRepository.setQueued(
            savedArticleId
        )
        notificationController.showArchiveQueued()


        val intent =
            Intent(
                context,
                ArchiveService::class.java
            ).apply {

                putExtra(
                    "saved_article_id",
                    savedArticleId
                )
            }

        ContextCompat.startForegroundService(
            context,
            intent
        )




        return Result.success(
            Unit
        )
    }

    suspend fun deleteArchive(
        savedArticleId: Long
    ) {
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