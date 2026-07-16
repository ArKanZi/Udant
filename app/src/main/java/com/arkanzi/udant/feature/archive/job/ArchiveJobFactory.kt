package com.arkanzi.udant.feature.archive.job

import android.content.Context
import com.arkanzi.udant.core.job.dispatcher.DownloadDispatcher
import com.arkanzi.udant.core.job.notification.DownloadNotification
import com.arkanzi.udant.core.job.registry.ArchiveJobRegistry
import com.arkanzi.udant.core.storage.StorageManager
import com.arkanzi.udant.feature.archive.model.ArchiveExecutionRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveJobFactory @Inject constructor(

    @param:ApplicationContext
    private val context: Context,
    private val storageManager: StorageManager,
    private val archiveJobRegistry: ArchiveJobRegistry,
    private val downloadDispatcher: DownloadDispatcher,
    private val downloadNotification: DownloadNotification

) {
    fun create(
        request: ArchiveExecutionRequest
    ): ArchiveJob = ArchiveJob(
        context = context,
        request = request,
        archiveJobRegistry = archiveJobRegistry,
        storageManager = storageManager,
        downloadDispatcher = downloadDispatcher,
        downloadNotification = downloadNotification
    )
}