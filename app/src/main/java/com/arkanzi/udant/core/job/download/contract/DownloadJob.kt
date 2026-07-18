package com.arkanzi.udant.core.job.download.contract

interface DownloadJob<T> {
    suspend fun execute(): T
}