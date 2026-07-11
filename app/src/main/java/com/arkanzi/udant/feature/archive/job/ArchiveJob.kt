package com.arkanzi.udant.feature.archive.job

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.arkanzi.udant.core.job.registry.ArchiveJobRegistry
import com.arkanzi.udant.core.job.worker.DownloadJob
import com.arkanzi.udant.core.storage.StorageManager
import com.arkanzi.udant.feature.archive.model.ArchiveExecutionRequest
import com.arkanzi.udant.feature.archive.model.ArchiveResponse
import com.arkanzi.udant.feature.archive.service.ArchiveService
import dagger.hilt.android.qualifiers.ApplicationContext

class ArchiveJob(
    @param:ApplicationContext
    private val context: Context,
    private val request: ArchiveExecutionRequest,
    private val archiveJobRegistry: ArchiveJobRegistry,
    private val storageManager: StorageManager,
) : DownloadJob<ArchiveResponse> {

    override suspend fun execute(): ArchiveResponse {

        val deferred =
            archiveJobRegistry.register(request.jobId)

        val intent = Intent(context, ArchiveService::class.java).apply {
            putExtra("job_id", request.jobId)
            putExtra("article_url", request.articleUrl)
        }

        ContextCompat.startForegroundService(context, intent)

        try {

            when (val result = deferred.await()) {

                is ArchiveResponse.Success -> {
                    val archiveUri = storageManager.moveArchiveToSaf(
                        request.jobId,
                        request.articleTitle
                    ) ?: throw IllegalStateException("Failed to move archive to SAF")

                    return result.copy(uri = archiveUri)
                }

                is ArchiveResponse.Failure -> {
                    throw result.throwable
                }
            }

        } finally {

            archiveJobRegistry.remove(
                request.jobId
            )
        }
    }
}