package com.arkanzi.udant.core.job.worker

interface DownloadJob<T> {
    suspend fun execute(): T
}