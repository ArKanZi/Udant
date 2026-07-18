package com.arkanzi.udant.core.job

import com.arkanzi.udant.core.job.download.DownloadManager
import com.arkanzi.udant.core.job.download.model.DownloadType
import com.arkanzi.udant.core.job.download.contract.DownloadPayload
import com.arkanzi.udant.core.job.download.model.DownloadRequest
import com.arkanzi.udant.core.job.contract.JobPayload
import com.arkanzi.udant.core.job.model.JobRequest
import com.arkanzi.udant.core.job.model.JobType
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class JobManager @Inject constructor(
    private val downloadManager: DownloadManager
) {

    suspend fun <T : JobPayload> enqueue(jobRequest: JobRequest<T>) {
        when (jobRequest.jobType) {
            JobType.DOWNLOAD -> downloadManager.enqueue(
                downloadRequest = DownloadRequest.Execute(
                    downloadType = DownloadType.ARCHIVE,
                    referenceId = jobRequest.referenceId,
                    payload = jobRequest.payload as DownloadPayload
                )
            )
        }

    }
}

//JobManager
//
//├── DownloadManager
//│     ├── ArchiveJob
//│     ├── PdfJob
//│     └── ExtensionJob
//│
//├── SyncManager
//│     ├── FeedSyncJob
//│     └── BookmarkSyncJob
//│
//└── CleanupManager
//└── CacheCleanupJob