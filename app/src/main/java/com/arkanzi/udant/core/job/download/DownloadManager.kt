package com.arkanzi.udant.core.job.download

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadManager @Inject constructor(
    private val downloadQueue: DownloadQueue
) {
    private val managerScope = CoroutineScope(
            SupervisorJob() + Dispatchers.IO
        )
    private var isRunning = false
    fun enqueue(job: DownloadJob){
        downloadQueue.enqueue(job)
        if (isRunning) {
            return
        }

        isRunning = true

        managerScope.launch {
            processQueue()
        }
    }

    private suspend fun processQueue() {
        while (!downloadQueue.isEmpty()) {

            val job = downloadQueue.dequeue()

            job?.execute()

        }

        isRunning = false
    }
}