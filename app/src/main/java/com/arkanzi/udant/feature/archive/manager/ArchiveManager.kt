package com.arkanzi.udant.feature.archive.manager

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.arkanzi.udant.core.model.ArchiveStatus
import com.arkanzi.udant.core.storage.StorageManager
import com.arkanzi.udant.feature.archive.model.ArchiveServiceResult
import com.arkanzi.udant.feature.archive.model.ArchiveUpdate
import com.arkanzi.udant.feature.archive.notification.ArchiveNotification
import com.arkanzi.udant.feature.archive.repository.ArchiveRepository
import com.arkanzi.udant.feature.archive.service.ArchiveService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveManager @Inject constructor(

    @param:ApplicationContext
    private val context: Context,

    private val archiveRepository: ArchiveRepository,

    private val archiveNotification: ArchiveNotification,

    private val storageManager: StorageManager

) {

    suspend fun processArchive(
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

        handleArchiveStatus(
            ArchiveUpdate.Queued(savedArticleId)
        )
        archiveNotification.showArchiveQueued()


        val intent =
            Intent(
                context,
                ArchiveService::class.java
            ).apply {

                putExtra("article_id", article.savedArticleId)
                putExtra("article_title", article.title)
                putExtra("article_url", article.articleUrl)
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

    suspend fun onArchiveServiceResult(result: ArchiveServiceResult) {
        archiveNotification.showArchiveProgress(
            text = "Processing archive...",
            indeterminate = true
        )
        when (result) {
            is ArchiveServiceResult.Success -> {
                finalizeArchive(result)
            }

            is ArchiveServiceResult.Failure -> {
                handleArchiveFailure(result)
            }
        }
    }

    private suspend fun finalizeArchive(result: ArchiveServiceResult.Success) = withContext(
        Dispatchers.IO
    ) {

        val safFolderUri = archiveRepository.getArchiveFolderUri().firstOrNull()
        archiveNotification.showArchiveProgress(
            text = "Copying to storage",
            indeterminate = true
        )

        try {
            val file =
                storageManager.createFileInSaf(
                    folderUri = safFolderUri?.toUri(),
                    fileName =result.fileName,
                    mimeType = "message/rfc822"
                )
            if (file == null) {
                handleArchiveFailure(
                    ArchiveServiceResult.Failure(
                        savedArticleId = result.savedArticleId,
                        throwable = IllegalStateException("Null File")
                    )
                )
                return@withContext
            } else {
                val status = storageManager
                    .copyToSaf(
                        localFile = result.localFile,
                        destinationUri = file.uri
                    )

                if (status) {
                    handleArchiveStatus(
                        ArchiveUpdate.Completed(
                            savedArticleId = result.savedArticleId,
                            archiveUri = file.uri.toString()
                        )
                    )
                    archiveNotification.showArchiveCompleted()
                }else {
                    handleArchiveFailure(
                        ArchiveServiceResult.Failure(
                            savedArticleId = result.savedArticleId,
                            throwable = IllegalStateException(
                                "copyToSaf failed"
                            )
                        )
                    )
                }
            }

           val deleteStatus =  storageManager.deleteLocalFile(result.localFile)
            if (!deleteStatus){
                Log.e("Archive Manager","Failed to delete temporary archive: ${result.localFile}")
            }

        } catch (e: Exception) {

            handleArchiveFailure(
                ArchiveServiceResult.Failure(
                    savedArticleId = result.savedArticleId,
                    throwable = e
                )
            )
            return@withContext
        }

    }

    private suspend fun handleArchiveFailure(result: ArchiveServiceResult.Failure) {
        handleArchiveStatus(
            ArchiveUpdate.Failed(result.savedArticleId)
        )

        archiveNotification.showArchiveFailed(result.throwable)

    }

    suspend fun handleArchiveStatus(update: ArchiveUpdate) {
        archiveRepository.setStatus(update)
    }


}