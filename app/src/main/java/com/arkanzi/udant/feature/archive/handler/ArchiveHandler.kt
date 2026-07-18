package com.arkanzi.udant.feature.archive.handler

import com.arkanzi.udant.core.database.entity.DownloadJobEntity
import com.arkanzi.udant.core.job.download.handler.DownloadHandler
import com.arkanzi.udant.core.job.download.model.DownloadManagerFailureReason
import com.arkanzi.udant.core.job.download.model.DownloadResponse
import com.arkanzi.udant.core.job.download.model.DownloadType
import com.arkanzi.udant.core.job.download.registry.DownloadPayloadCodecRegistry
import com.arkanzi.udant.feature.archive.job.ArchiveJobFactory
import com.arkanzi.udant.feature.archive.model.ArchiveExecutionRequest
import com.arkanzi.udant.feature.archive.model.ArchiveRequestPayload
import com.arkanzi.udant.feature.archive.model.ArchiveResponsePayload
import com.arkanzi.udant.feature.archive.model.ArchiveResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveHandler @Inject constructor(

    private val archiveJobFactory: ArchiveJobFactory,
    private val downloadPayloadCodecRegistry: DownloadPayloadCodecRegistry

) : DownloadHandler<ArchiveResponsePayload>{

    override suspend fun execute(
        job: DownloadJobEntity
    ): DownloadResponse<ArchiveResponsePayload> {

        val codec = downloadPayloadCodecRegistry.get(job.jobType)

        val payload = runCatching {
            codec.deserialize(job.payload) as ArchiveRequestPayload
        }.getOrElse { throwable ->
            return DownloadResponse.Failure(
                jobId = job.jobId,
                downloadType = DownloadType.ARCHIVE,
                timestamp = System.currentTimeMillis(),
                header = "Archiving Failed to Start",
                source = ArchiveHandler::class,
                reason = DownloadManagerFailureReason.PayloadDeserializationFailed,
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

                DownloadResponse.Success(
                    jobId = result.jobId,
                    downloadType = DownloadType.ARCHIVE,
                    timestamp = System.currentTimeMillis(),
                    payload = ArchiveResponsePayload(
                        savedArticleId = job.referenceId,
                        archiveUri = archiveUri
                    )
                )
            }

            is ArchiveResponse.Failure -> {

                DownloadResponse.Failure(
                    jobId = result.jobId,
                    downloadType = DownloadType.ARCHIVE,
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