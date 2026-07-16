package com.arkanzi.udant.core.job

import com.arkanzi.udant.core.database.entity.DownloadJobEntity
import com.arkanzi.udant.core.job.dispatcher.DownloadDispatcher
import com.arkanzi.udant.core.job.event.Events
import com.arkanzi.udant.core.job.model.DownloadFailureReason
import com.arkanzi.udant.core.job.model.DownloadJobResponse
import com.arkanzi.udant.core.job.model.DownloadJobStatus
import com.arkanzi.udant.core.job.model.DownloadManagerLogFormatter
import com.arkanzi.udant.core.job.model.DownloadPayload
import com.arkanzi.udant.core.job.model.DownloadRequest
import com.arkanzi.udant.core.job.model.ProgressState
import com.arkanzi.udant.core.job.registry.DownloadJobHandlerRegistry
import com.arkanzi.udant.core.job.registry.PayloadCodecRegistry
import com.arkanzi.udant.core.job.repository.DownloadJobRepository
import com.arkanzi.udant.core.logging.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadManager @Inject constructor(
    private val downloadJobRepository: DownloadJobRepository,
    private val downloadDispatcher: DownloadDispatcher,
    private val handlerRegistry: DownloadJobHandlerRegistry,
    private val payloadCodecRegistry: PayloadCodecRegistry,
    private val appLogger: AppLogger

) {
    private val managerScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )
    private var isRunning = false

    init {
        appLogger.debug(
            DownloadManager::class,
            "DownloadManager created"
        )

    }


    suspend fun <T : DownloadPayload> enqueue(downloadRequest: DownloadRequest<T>) {

        val codec = payloadCodecRegistry.get(downloadRequest.downloadJobType)

        val payload = runCatching {
            codec.serialize(downloadRequest.payload)
        }.getOrElse { throwable ->
            appLogger.error(
                tag = DownloadManager::class,
                message = DownloadManagerLogFormatter.formatFailure(
                    jobId = null,
                    header = "Queue Operation Failed",
                    downloadJobType = downloadRequest.downloadJobType,
                    reason = DownloadFailureReason.PayloadSerializationFailed,
                    source = DownloadManager::class,
                    exception = throwable::class.java.simpleName,
                ),
                throwable = throwable
            )
            return
        }

        val downloadJobEntry = downloadJobRepository.getDownloadJobByReferenceId(
            downloadRequest.referenceId,
            downloadRequest.downloadJobType
        )
        if (downloadJobEntry != null) {
            if (downloadJobEntry.status == DownloadJobStatus.FAILED) {
                downloadJobRepository.updateStatus(
                    downloadJobEntry.jobId,
                    status = DownloadJobStatus.QUEUED
                )
            }

        } else {
            val now = System.currentTimeMillis()
            val job = DownloadJobEntity(
                jobId = UUID.randomUUID().toString(),
                referenceId = downloadRequest.referenceId,
                jobType = downloadRequest.downloadJobType,
                status = DownloadJobStatus.QUEUED,
                payload = payload,
                createdAt = now,
                updatedAt = now
            )

            downloadJobRepository.insertJob(job)
        }



        if (isRunning) return

        isRunning = true

        managerScope.launch {
            processNextJob()
        }
    }

    private suspend fun processNextJob() {

        val job = downloadJobRepository.getNextJobByStatus(
            DownloadJobStatus.QUEUED
        )

        if (job == null) {
            isRunning = false
            return
        }

        downloadJobRepository.updateStatus(
            jobId = job.jobId,
            status = DownloadJobStatus.RUNNING
        )


        val handler = handlerRegistry.get(job.jobType)

        when (val result = handler.execute(job)) {

            is DownloadJobResponse.Success -> {

                downloadJobRepository.updateStatus(
                    jobId = job.jobId,
                    status = DownloadJobStatus.COMPLETED
                )

                downloadDispatcher.emitProgress(
                    ProgressState.Completed(notificationId = result.jobId.hashCode())
                )

                downloadDispatcher.emitEvent(
                    Events.Completed(
                        jobId = result.jobId,
                        jobType = job.jobType,
                        payload = result.payload
                    )
                )

            }

            is DownloadJobResponse.Failure -> {

                appLogger.error(
                    tag = DownloadManager::class,
                    message = DownloadManagerLogFormatter.formatFailure(
                        jobId = result.jobId,
                        header = result.header,
                        downloadJobType = result.downloadJobType,
                        source = result.source,
                        reason = result.reason,
                        exception = result.throwable::class.java.simpleName
                    ),
                    throwable = result.throwable
                )

                downloadJobRepository.updateStatus(
                    jobId = job.jobId,
                    status = DownloadJobStatus.FAILED
                )

                downloadDispatcher.emitProgress(
                    ProgressState.Failed(
                        notificationId = result.jobId.hashCode(), reason = result.reason
                    )
                )

                downloadDispatcher.emitEvent(
                    Events.Failed(
                        jobId = result.jobId,
                        jobType = job.jobType,
                        throwable = result.throwable
                    )
                )
            }
        }
        downloadJobRepository.deleteJob(job.jobId)



        processNextJob()
    }
}