package com.arkanzi.udant.feature.archive.job

import com.arkanzi.udant.feature.archive.model.ArchiveServiceResult
import kotlinx.coroutines.CompletableDeferred
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveJobRegistry @Inject constructor() {

    private val jobs =
        ConcurrentHashMap<Long, CompletableDeferred<ArchiveServiceResult>>()

    fun register(
        articleId: Long
    ): CompletableDeferred<ArchiveServiceResult> {

        val deferred = CompletableDeferred<ArchiveServiceResult>()

        jobs[articleId] = deferred

        return deferred
    }

    fun complete(
        articleId: Long,
        result: ArchiveServiceResult
    ) {

        jobs[articleId]?.complete(result)

    }

    fun remove(
        articleId: Long
    ) {

        jobs.remove(articleId)

    }
}