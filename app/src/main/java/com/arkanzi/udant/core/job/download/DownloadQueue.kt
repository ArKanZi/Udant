package com.arkanzi.udant.core.job.download

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadQueue @Inject constructor() {
    private val queue = ArrayDeque<DownloadJob>()
    fun enqueue(job: DownloadJob){
        queue.add(job)
    }
    fun dequeue(): DownloadJob? {
        return queue.removeFirstOrNull()
    }

    fun move(fromIndex:Int,toIndex: Int){
        TODO()
    }

    fun remove(job: DownloadJob){
        queue.remove(job)
    }

    fun peek(): DownloadJob? {
        return queue.firstOrNull()
    }
    fun isEmpty() = queue.isEmpty()

}