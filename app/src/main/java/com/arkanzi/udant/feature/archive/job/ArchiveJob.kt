package com.arkanzi.udant.feature.archive.job

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.arkanzi.udant.core.job.dispatcher.DownloadDispatcher
import com.arkanzi.udant.core.job.model.ProgressState
import com.arkanzi.udant.core.job.notification.DownloadNotification
import com.arkanzi.udant.core.job.registry.ArchiveJobRegistry
import com.arkanzi.udant.core.job.worker.DownloadJob
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
    private val archiveJobRegistry: ArchiveJobRegistry,
    private val storageManager: StorageManager,
    private val downloadDispatcher: DownloadDispatcher,
    private val downloadNotification: DownloadNotification
) : DownloadJob<ArchiveResponse> {

    override suspend fun execute(): ArchiveResponse {

        val deferred =
            archiveJobRegistry.register(request.jobId)

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
                reason = ArchiveFailureReason.ArchiveJob.ServiceStartFailed,
                throwable = throwable
            )
        }

        try {

            when (val result = deferred.await()) {

                is ArchiveResponse.Success -> {

                    return runCatching {
                        downloadDispatcher.emitProgress(
                            ProgressState.Moving(
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
                            reason = ArchiveFailureReason.ArchiveJob.MoveToSafFailed,
                            throwable = throwable
                        )
                    }
                }

                is ArchiveResponse.Failure -> return result
            }

        } finally {

            archiveJobRegistry.remove(request.jobId)
        }
    }
}