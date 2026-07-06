package com.arkanzi.udant.core.job

import com.arkanzi.udant.core.job.download.DownloadJob
import com.arkanzi.udant.core.job.download.DownloadManager
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class JobManager @Inject constructor(
    private val downloadManger: DownloadManager
){
    fun download(job: DownloadJob){
        downloadManger.enqueue(job)
    }
}