package com.arkanzi.udant.feature.archive.handler

import com.arkanzi.udant.core.database.entity.DownloadJobEntity
import com.arkanzi.udant.core.job.download.handler.DownloadHandler
import com.arkanzi.udant.core.job.download.model.DownloadAction
import com.arkanzi.udant.core.job.download.model.DownloadManagerFailureReason
import com.arkanzi.udant.core.job.download.model.DownloadResult
import com.arkanzi.udant.core.job.download.model.DownloadType
import com.arkanzi.udant.core.job.download.codec.DownloadPayloadCodecRegistry
import com.arkanzi.udant.feature.archive.job.ArchiveJobFactory
import com.arkanzi.udant.feature.archive.model.ArchiveAction
import com.arkanzi.udant.feature.archive.model.ArchiveExecutionRequest
import com.arkanzi.udant.feature.archive.model.ArchiveRequestPayload
import com.arkanzi.udant.feature.archive.model.ArchiveResultPayload
import com.arkanzi.udant.feature.archive.model.ArchiveResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveHandler @Inject constructor(

    private val archiveJobFactory: ArchiveJobFactory,
    private val downloadPayloadCodecRegistry: DownloadPayloadCodecRegistry

) : DownloadHandler<ArchiveResultPayload>{

    override suspend fun execute(
        action: DownloadAction,
        job: DownloadJobEntity
    ): DownloadResult<ArchiveResultPayload> {

        val codec = downloadPayloadCodecRegistry.get(job.jobType)

        val payload = runCatching {
            codec.deserialize(job.payload) as ArchiveRequestPayload
        }.getOrElse { throwable ->
            return DownloadResult.Failure(
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
            action = if(action == DownloadAction.START) ArchiveAction.START else ArchiveAction.STOP ,
            jobId = job.jobId,
            articleUrl = payload.articleUrl,
            articleTitle = payload.articleTitle
        )

        val archiveJob = archiveJobFactory.create(request)

        return when (val result = archiveJob.execute()) {

            is ArchiveResult.Success -> {

                val archiveUri = checkNotNull(result.uri) {
                    "ArchiveJob returned Success without archiveUri"
                }

                DownloadResult.Success(
                    jobId = result.jobId,
                    downloadType = DownloadType.ARCHIVE,
                    timestamp = result.timestamp,
                    payload = ArchiveResultPayload(
                        savedArticleId = job.referenceId,
                        archiveUri = archiveUri
                    )
                )
            }

            is ArchiveResult.Failure -> {

                DownloadResult.Failure(
                    jobId = result.jobId,
                    downloadType = DownloadType.ARCHIVE,
                    timestamp = result.timestamp,
                    header = result.header,
                    source = result.source,
                    reason = result.reason,
                    throwable = result.throwable
                )
            }
            is ArchiveResult.Paused ->{
                DownloadResult.Paused(
                    jobId = result.jobId,
                    downloadType = DownloadType.ARCHIVE,
                    timestamp = result.timestamp,
                    payload = ArchiveResultPayload(
                        savedArticleId = job.referenceId,
                    )
                )
            }
        }
    }
}