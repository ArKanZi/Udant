package com.arkanzi.udant.feature.archive.job

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.arkanzi.udant.core.job.download.DownloadJob
import com.arkanzi.udant.feature.archive.manager.ArchiveManager
import com.arkanzi.udant.feature.archive.model.ArchiveJobRequest
import com.arkanzi.udant.feature.archive.model.ArchiveUpdate
import com.arkanzi.udant.feature.archive.service.ArchiveService
import dagger.hilt.android.qualifiers.ApplicationContext

class ArchiveJob(
    @param:ApplicationContext
    private val context: Context,
    private val request: ArchiveJobRequest,
    private val archiveManager: ArchiveManager,
    private val archiveJobRegistry: ArchiveJobRegistry
) : DownloadJob {

    override suspend fun execute() {

        if (!archiveManager.hasNotificationPermission()) {
            return
        }

        if (!archiveManager.hasStoragePermission()) {
            return
        }

        archiveManager.handleArchiveStatus(
            ArchiveUpdate.Archiving(
                savedArticleId = request.articleId
            )
        )

        val deferred =
            archiveJobRegistry.register(
                request.articleId
            )

        val intent = Intent(
            context,
            ArchiveService::class.java
        ).apply {
            putExtra("article_id", request.articleId)
            putExtra("article_title", request.articleTitle)
            putExtra("article_url", request.articleUrl)
        }

        ContextCompat.startForegroundService(
            context,
            intent
        )
        Log.d("ArchiveJob", "Waiting...")

        val result = deferred.await()

        Log.d("ArchiveJob", "Resumed")


        archiveManager.onArchiveServiceResult(result)

        archiveJobRegistry.remove(request.articleId)
    }
}