package com.arkanzi.udant.feature.archive.job

import android.content.Context
import com.arkanzi.udant.core.job.download.dispatcher.DownloadDispatcher
import com.arkanzi.udant.core.job.download.notification.DownloadNotification
import com.arkanzi.udant.feature.archive.registry.ArchiveRegistry
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
    private val archiveRegistry: ArchiveRegistry,
    private val downloadDispatcher: DownloadDispatcher,
    private val downloadNotification: DownloadNotification

) {
    fun create(
        request: ArchiveExecutionRequest
    ): ArchiveJob = ArchiveJob(
        context = context,
        request = request,
        archiveRegistry = archiveRegistry,
        storageManager = storageManager,
        downloadDispatcher = downloadDispatcher,
        downloadNotification = downloadNotification
    )
}