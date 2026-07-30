package com.arkanzi.udant.feature.archive.job

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.arkanzi.udant.core.job.download.dispatcher.DownloadDispatcher
import com.arkanzi.udant.core.job.download.model.DownloadProgressState
import com.arkanzi.udant.feature.archive.registry.ArchiveRegistry
import com.arkanzi.udant.core.job.download.contract.DownloadJob
import com.arkanzi.udant.core.storage.StorageManager
import com.arkanzi.udant.feature.archive.model.ArchiveAction
import com.arkanzi.udant.feature.archive.model.ArchiveExecutionRequest
import com.arkanzi.udant.feature.archive.model.ArchiveFailureReason
import com.arkanzi.udant.feature.archive.model.ArchiveResult
import com.arkanzi.udant.feature.archive.service.ArchiveContract
import com.arkanzi.udant.feature.archive.service.ArchiveService
import dagger.hilt.android.qualifiers.ApplicationContext

class ArchiveJob(
    @param:ApplicationContext
    private val context: Context,
    private val request: ArchiveExecutionRequest,
    private val archiveRegistry: ArchiveRegistry,
    private val storageManager: StorageManager,
    private val downloadDispatcher: DownloadDispatcher
) : DownloadJob<ArchiveResult> {

    override suspend fun execute(): ArchiveResult {


        val deferred =
            archiveRegistry.register(request.jobId)

        val intent = Intent(context, ArchiveService::class.java).apply {
            action =
                if (request.action == ArchiveAction.START) ArchiveContract.ACTION_START
                else ArchiveContract.ACTION_STOP
            putExtra(ArchiveContract.EXTRA_JOB_ID, request.jobId)
            putExtra(ArchiveContract.EXTRA_ARTICLE_URL, request.articleUrl)
        }

        runCatching {
            ContextCompat.startForegroundService(context, intent)
        }.getOrElse { throwable ->
            return ArchiveResult.Failure(
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

                is ArchiveResult.Success -> {

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

                        ArchiveResult.Failure(
                            jobId = request.jobId,
                            timestamp = System.currentTimeMillis(),
                            header = "Creating File in SAF Failed",
                            source = ArchiveJob::class,
                            reason = ArchiveFailureReason.Archive.MoveToSafFailed,
                            throwable = throwable
                        )
                    }
                }

                is ArchiveResult.Failure -> return result

                is ArchiveResult.Paused -> return result

            }

        } finally {

            archiveRegistry.remove(request.jobId)
        }
    }
}