package com.arkanzi.udant.feature.archive.job

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.arkanzi.udant.core.job.download.dispatcher.DownloadDispatcher
import com.arkanzi.udant.core.job.download.model.DownloadProgressState
import com.arkanzi.udant.core.job.download.notification.DownloadNotification
import com.arkanzi.udant.feature.archive.registry.ArchiveRegistry
import com.arkanzi.udant.core.job.download.contract.DownloadJob
import com.arkanzi.udant.core.storage.StorageManager
import com.arkanzi.udant.feature.archive.model.ArchiveExecutionRequest
import com.arkanzi.udant.feature.archive.model.ArchiveFailureReason
import com.arkanzi.udant.feature.archive.model.ArchiveResponse
import com.arkanzi.udant.feature.archive.service.ArchiveService
import dagger.hilt.android.qualifiers.ApplicationContext

class ArchiveJob(
    @param:ApplicationContext
    private val context: Context,
    private val request: ArchiveExecutionRequest,
    private val archiveRegistry: ArchiveRegistry,
    private val storageManager: StorageManager,
    private val downloadDispatcher: DownloadDispatcher,
    private val downloadNotification: DownloadNotification
) : DownloadJob<ArchiveResponse> {

    override suspend fun execute(): ArchiveResponse {

        val deferred =
            archiveRegistry.register(request.jobId)

        val intent = Intent(context, ArchiveService::class.java).apply {
            putExtra("job_id", request.jobId)
            putExtra("article_url", request.articleUrl)
        }

        runCatching {
            ContextCompat.startForegroundService(context, intent)
        }.getOrElse {throwable ->
            return ArchiveResponse.Failure(
                jobId = request.jobId,
                timestamp = System.currentTimeMillis(),
                header = "Archive Service Failed to Start",
                source = ArchiveJob::class,
                reason = ArchiveFailureReason.Archive.ServiceStartFailed,
                throwable = throwable
            )
        }

        try {

            when (val result = deferred.await()) {

                is ArchiveResponse.Success -> {

                    return runCatching {
                        downloadDispatcher.emitProgress(
                            DownloadProgressState.Moving(
                                notificationId = result.jobId.hashCode()
                            )
                        )

                        val archiveUri = storageManager.moveArchiveToSaf(
                            request.jobId,
                            request.articleTitle
                        )

                        result.copy(uri = archiveUri)

                    }.getOrElse { throwable ->

                        ArchiveResponse.Failure(
                            jobId = request.jobId,
                            timestamp = System.currentTimeMillis(),
                            header = "Creating File in SAF Failed",
                            source = ArchiveJob::class,
                            reason = ArchiveFailureReason.Archive.MoveToSafFailed,
                            throwable = throwable
                        )
                    }
                }

                is ArchiveResponse.Failure -> return result
            }

        } finally {

            archiveRegistry.remove(request.jobId)
        }
    }
}