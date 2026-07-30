package com.arkanzi.udant.feature.archive.registry

import com.arkanzi.udant.feature.archive.model.ArchiveResult
import kotlinx.coroutines.CompletableDeferred
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveRegistry @Inject constructor() {

    private val jobs =
        ConcurrentHashMap<String, CompletableDeferred<ArchiveResult>>()

    fun register(
        jobId: String
    ): CompletableDeferred<ArchiveResult> {

        val deferred = CompletableDeferred<ArchiveResult>()

        jobs[jobId] = deferred

        return deferred
    }

    fun complete(
        jobId: String,
        result: ArchiveResult
    ) {

        jobs[jobId]?.complete(result)

    }

    fun remove(
        jobId: String
    ) {

        jobs.remove(jobId)

    }
}