package com.arkanzi.udant.feature.archive.handler

import com.arkanzi.udant.core.database.entity.DownloadJobEntity
import com.arkanzi.udant.core.job.handler.DownloadJobHandler
import com.arkanzi.udant.core.job.model.DownloadFailureReason
import com.arkanzi.udant.core.job.model.DownloadJobResponse
import com.arkanzi.udant.core.job.model.DownloadJobType
import com.arkanzi.udant.core.job.registry.PayloadCodecRegistry
import com.arkanzi.udant.feature.archive.job.ArchiveJobFactory
import com.arkanzi.udant.feature.archive.model.ArchiveExecutionRequest
import com.arkanzi.udant.feature.archive.model.ArchiveRequestPayload
import com.arkanzi.udant.feature.archive.model.ArchiveResponsePayload
import com.arkanzi.udant.feature.archive.model.ArchiveResponse
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

        val payload = runCatching {
            codec.deserialize(job.payload) as ArchiveRequestPayload
        }.getOrElse { throwable ->
            return DownloadJobResponse.Failure(
                jobId = job.jobId,
                downloadJobType = DownloadJobType.ARCHIVE,
                timestamp = System.currentTimeMillis(),
                header = "Archiving Failed to Start",
                source = ArchiveJobHandler::class,
                reason = DownloadFailureReason.PayloadDeserializationFailed,
                throwable = throwable
            )
        }

        val request = ArchiveExecutionRequest(
            jobId = job.jobId,
            articleUrl = payload.articleUrl,
            articleTitle = payload.articleTitle
        )

        val archiveJob = archiveJobFactory.create(request)

        return when (val result = archiveJob.execute()) {

            is ArchiveResponse.Success -> {

                val archiveUri = checkNotNull(result.uri) {
                    "ArchiveJob returned Success without archiveUri"
                }

                DownloadJobResponse.Success(
                    jobId = result.jobId,
                    downloadJobType = DownloadJobType.ARCHIVE,
                    timestamp = System.currentTimeMillis(),
                    payload = ArchiveResponsePayload(
                        savedArticleId = job.referenceId,
                        archiveUri = archiveUri
                    )
                )
            }

            is ArchiveResponse.Failure -> {

                DownloadJobResponse.Failure(
                    jobId = result.jobId,
                    downloadJobType = DownloadJobType.ARCHIVE,
                    timestamp = System.currentTimeMillis(),
                    header = result.header,
                    source = result.source,
                    reason = result.reason,
                    throwable = result.throwable
                )
            }
        }
    }
}