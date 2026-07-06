package com.arkanzi.udant.feature.archive.job

import android.content.Context
import com.arkanzi.udant.feature.archive.manager.ArchiveManager
import com.arkanzi.udant.feature.archive.model.ArchiveJobRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveJobFactory @Inject constructor(

    @param:ApplicationContext
    private val context: Context,

    private val archiveManager: ArchiveManager,

    private val archiveJobRegistry: ArchiveJobRegistry

) {
    fun create(
        request: ArchiveJobRequest
    ): ArchiveJob = ArchiveJob(
        context = context,
        request = request,
        archiveManager = archiveManager,
        archiveJobRegistry = archiveJobRegistry
    )
}