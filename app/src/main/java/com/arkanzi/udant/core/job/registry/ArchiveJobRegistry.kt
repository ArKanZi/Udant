package com.arkanzi.udant.core.job.registry

import com.arkanzi.udant.feature.archive.model.ArchiveResponse
import kotlinx.coroutines.CompletableDeferred
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveJobRegistry @Inject constructor() {

    private val jobs =
        ConcurrentHashMap<String, CompletableDeferred<ArchiveResponse>>()

    fun register(
        jobId: String
    ): CompletableDeferred<ArchiveResponse> {

        val deferred = CompletableDeferred<ArchiveResponse>()

        jobs[jobId] = deferred

        return deferred
    }

    fun complete(
        jobId: String,
        result: ArchiveResponse
    ) {

        jobs[jobId]?.complete(result)

    }

    fun remove(
        jobId: String
    ) {

        jobs.remove(jobId)

    }
}