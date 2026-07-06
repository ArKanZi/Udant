package com.arkanzi.udant.core.job.download

interface DownloadJob {
    suspend fun execute()
}