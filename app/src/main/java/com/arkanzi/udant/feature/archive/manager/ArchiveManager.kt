package com.arkanzi.udant.feature.archive.manager

import androidx.core.net.toUri
import com.arkanzi.udant.core.job.JobManager
import com.arkanzi.udant.core.job.dispatcher.DownloadDispatcher
import com.arkanzi.udant.core.job.event.Events
import com.arkanzi.udant.core.job.model.DownloadJobType
import com.arkanzi.udant.core.job.model.JobRequest
import com.arkanzi.udant.core.job.model.JobType
import com.arkanzi.udant.core.logging.AppLogger
import com.arkanzi.udant.core.model.ArchiveStatus
import com.arkanzi.udant.core.storage.StorageManager
import com.arkanzi.udant.core.system.SystemChecker
import com.arkanzi.udant.feature.archive.model.ArchiveRequestPayload
import com.arkanzi.udant.feature.archive.model.ArchiveRequest
import com.arkanzi.udant.feature.archive.model.ArchiveResponsePayload
import com.arkanzi.udant.feature.archive.model.ArchiveUpdate
import com.arkanzi.udant.feature.archive.repository.ArchiveRepository
import com.arkanzi.udant.feature.savedArticles.repository.SavedArticlesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveManager @Inject constructor(

    private val systemChecker: SystemChecker,

    private val archiveRepository: ArchiveRepository,

    private val storageManager: StorageManager,

    private val jobManager: JobManager,

    private val savedArticlesRepository: SavedArticlesRepository,

    private val downloadDispatcher: DownloadDispatcher,

    private val appLogger: AppLogger
) {
    private val managerScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    init {
        appLogger.debug(
            ArchiveManager::class,
            "ArchiveManager created"
        )

        observeDownloadEvents()
    }

    private fun observeDownloadEvents() {

        managerScope.launch {
            downloadDispatcher.events.collect { event ->

                when (event) {

                    is Events.Completed -> {

                        if (event.jobType != DownloadJobType.ARCHIVE) {
                            return@collect
                        }

                        val payload = event.payload as ArchiveResponsePayload

                        savedArticlesRepository.updateArchive(
                            savedArticleId = payload.savedArticleId,
                            archiveUri = payload.archiveUri,
                            archiveStatus = ArchiveStatus.COMPLETED
                        )
                    }

                    is Events.Failed -> {

                        if (event.jobType != DownloadJobType.ARCHIVE) {
                            return@collect
                        }

                        // TODO
                    }
                }
            }
        }
    }

    suspend fun archive(request: ArchiveRequest) {
//        Business checks TODO()

        if (!systemChecker.hasStoragePermission()) {
            return
        }

        handleArchiveStatus(
            update = ArchiveUpdate.Queued(
                savedArticleId = request.savedArticleId
            )
        )

        jobManager.enqueue(
            jobRequest = JobRequest.Execute(
                jobType = JobType.DOWNLOAD,
                referenceId = request.savedArticleId,
                payload = ArchiveRequestPayload(
                    articleTitle = request.articleTitle,
                    articleUrl = request.articleUrl
                )
            )
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

    suspend fun handleArchiveStatus(update: ArchiveUpdate) {
        archiveRepository.setStatus(update)
    }


}