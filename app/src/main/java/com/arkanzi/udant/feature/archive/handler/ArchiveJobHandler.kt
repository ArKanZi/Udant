package com.arkanzi.udant.feature.archive.handler

import android.util.Log
import com.arkanzi.udant.core.database.entity.DownloadJobEntity
import com.arkanzi.udant.core.job.handler.DownloadJobHandler
import com.arkanzi.udant.core.job.model.DownloadJobResponse
import com.arkanzi.udant.core.job.registry.PayloadCodecRegistry
import com.arkanzi.udant.feature.archive.job.ArchiveJobFactory
import com.arkanzi.udant.feature.archive.model.ArchiveExecutionRequest
import com.arkanzi.udant.feature.archive.model.ArchiveRequestPayload
import com.arkanzi.udant.feature.archive.model.ArchiveResponsePayload
import com.arkanzi.udant.feature.archive.model.ArchiveResponse
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveJobHandler @Inject constructor(

    private val archiveJobFactory: ArchiveJobFactory,
    private val payloadCodecRegistry: PayloadCodecRegistry

) : DownloadJobHandler<ArchiveResponsePayload>{

    override suspend fun execute(
        job: DownloadJobEntity
    ): DownloadJobResponse<ArchiveResponsePayload> {

        val codec = payloadCodecRegistry.get(job.jobType)

        val payload = codec.deserialize(job.payload) as ArchiveRequestPayload

        val request = ArchiveExecutionRequest(
            jobId = job.jobId,
            articleUrl = payload.articleUrl,
            articleTitle = payload.articleTitle
        )

        Log.d("ArchiveHandler", "Creating ArchiveJob")

        val archiveJob = archiveJobFactory.create(request)

        Log.d("ArchiveHandler", "Executing ArchiveJob")

        return when (val result = archiveJob.execute()) {

            is ArchiveResponse.Success -> {

                val archiveUri = checkNotNull(result.uri) {
                    "ArchiveJob returned Success without archiveUri"
                }
                Log.d("ArchiveHandler", "Archive completed")

                DownloadJobResponse.Success(
                    jobId = result.jobId,
                    payload = ArchiveResponsePayload(
                        savedArticleId = job.referenceId,
                        archiveUri = archiveUri
                    )
                )
            }

            is ArchiveResponse.Failure -> {

                Log.e(
                    "ArchiveHandler",
                    "Archive failed",
                    result.throwable
                )

                DownloadJobResponse.Failure(
                    jobId = result.jobId,
                    throwable = result.throwable
                )
            }
        }
    }
}