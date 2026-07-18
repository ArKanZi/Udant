package com.arkanzi.udant.core.job.download

import com.arkanzi.udant.core.database.entity.DownloadJobEntity
import com.arkanzi.udant.core.job.download.dispatcher.DownloadDispatcher
import com.arkanzi.udant.core.job.download.event.DownloadEvents
import com.arkanzi.udant.core.job.download.model.DownloadManagerFailureReason
import com.arkanzi.udant.core.job.download.model.DownloadResponse
import com.arkanzi.udant.core.job.download.model.DownloadStatus
import com.arkanzi.udant.core.job.download.logging.DownloadSystemLogFormatter
import com.arkanzi.udant.core.job.download.contract.DownloadPayload
import com.arkanzi.udant.core.job.download.model.DownloadRequest
import com.arkanzi.udant.core.job.download.model.DownloadProgressState
import com.arkanzi.udant.core.job.download.registry.DownloadHandlerRegistry
import com.arkanzi.udant.core.job.download.registry.DownloadPayloadCodecRegistry
import com.arkanzi.udant.core.job.download.repository.DownloadRepository
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
    private val downloadRepository: DownloadRepository,
    private val downloadDispatcher: DownloadDispatcher,
    private val handlerRegistry: DownloadHandlerRegistry,
    private val downloadPayloadCodecRegistry: DownloadPayloadCodecRegistry,
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

        val codec = downloadPayloadCodecRegistry.get(downloadRequest.downloadType)

        val payload = runCatching {
            codec.serialize(downloadRequest.payload)
        }.getOrElse { throwable ->
            appLogger.error(
                tag = DownloadManager::class,
                message = DownloadSystemLogFormatter.formatFailure(
                    jobId = null,
                    header = "Queue Operation Failed",
                    downloadType = downloadRequest.downloadType,
                    reason = DownloadManagerFailureReason.PayloadSerializationFailed,
                    source = DownloadManager::class,
                    exception = throwable::class.java.simpleName,
                ),
                throwable = throwable
            )
            return
        }

        val downloadJobEntry = downloadRepository.getDownloadJobByReferenceId(
            downloadRequest.referenceId,
            downloadRequest.downloadType
        )
        if (downloadJobEntry != null) {
            if (downloadJobEntry.status == DownloadStatus.FAILED) {
                downloadRepository.updateStatus(
                    downloadJobEntry.jobId,
                    status = DownloadStatus.QUEUED
                )
            }

        } else {
            val now = System.currentTimeMillis()
            val job = DownloadJobEntity(
                jobId = UUID.randomUUID().toString(),
                referenceId = downloadRequest.referenceId,
                jobType = downloadRequest.downloadType,
                status = DownloadStatus.QUEUED,
                payload = payload,
                createdAt = now,
                updatedAt = now
            )

            downloadRepository.insertJob(job)
        }



        if (isRunning) return

        isRunning = true

        managerScope.launch {
            processNextJob()
        }
    }

    private suspend fun processNextJob() {

        val job = downloadRepository.getNextJobByStatus(
            DownloadStatus.QUEUED
        )

        if (job == null) {
            isRunning = false
            return
        }

        downloadRepository.updateStatus(
            jobId = job.jobId,
            status = DownloadStatus.RUNNING
        )


        val handler = handlerRegistry.get(job.jobType)

        when (val result = handler.execute(job)) {

            is DownloadResponse.Success -> {

                downloadRepository.updateStatus(
                    jobId = job.jobId,
                    status = DownloadStatus.COMPLETED
                )

                downloadDispatcher.emitProgress(
                    DownloadProgressState.Completed(notificationId = result.jobId.hashCode())
                )

                downloadDispatcher.emitEvent(
                    DownloadEvents.Completed(
                        jobId = result.jobId,
                        jobType = job.jobType,
                        payload = result.payload
                    )
                )

            }

            is DownloadResponse.Failure -> {

                appLogger.error(
                    tag = DownloadManager::class,
                    message = DownloadSystemLogFormatter.formatFailure(
                        jobId = result.jobId,
                        header = result.header,
                        downloadType = result.downloadType,
                        source = result.source,
                        reason = result.reason,
                        exception = result.throwable::class.java.simpleName
                    ),
                    throwable = result.throwable
                )

                downloadRepository.updateStatus(
                    jobId = job.jobId,
                    status = DownloadStatus.FAILED
                )

                downloadDispatcher.emitProgress(
                    DownloadProgressState.Failed(
                        notificationId = result.jobId.hashCode(), reason = result.reason
                    )
                )

                downloadDispatcher.emitEvent(
                    DownloadEvents.Failed(
                        jobId = result.jobId,
                        jobType = job.jobType,
                        throwable = result.throwable
                    )
                )
            }
        }
        downloadRepository.deleteJob(job.jobId)



        processNextJob()
    }
}