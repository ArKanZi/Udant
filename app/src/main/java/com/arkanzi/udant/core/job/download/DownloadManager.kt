package com.arkanzi.udant.core.job.download

import com.arkanzi.udant.core.database.entity.DownloadJobEntity
import com.arkanzi.udant.core.job.download.dispatcher.DownloadDispatcher
import com.arkanzi.udant.core.job.download.event.DownloadEvents
import com.arkanzi.udant.core.job.download.model.DownloadManagerFailureReason
import com.arkanzi.udant.core.job.download.model.DownloadResult
import com.arkanzi.udant.core.job.download.model.DownloadStatus
import com.arkanzi.udant.core.job.download.logging.DownloadSystemLogFormatter
import com.arkanzi.udant.core.job.download.contract.DownloadPayload
import com.arkanzi.udant.core.job.download.model.DownloadAction
import com.arkanzi.udant.core.job.download.model.DownloadRequest
import com.arkanzi.udant.core.job.download.model.DownloadProgressState
import com.arkanzi.udant.core.job.download.handler.DownloadHandlerRegistry
import com.arkanzi.udant.core.job.download.codec.DownloadPayloadCodecRegistry
import com.arkanzi.udant.core.job.download.repository.DownloadRepository
import com.arkanzi.udant.core.job.download.router.DownloadResultRouter
import com.arkanzi.udant.core.logging.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val downloadResultRouter: DownloadResultRouter,
    private val appLogger: AppLogger

) {
    private val managerScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    init {
        appLogger.debug(
            DownloadManager::class,
            "DownloadManager created"
        )

    }


    suspend fun <T : DownloadPayload> enqueue(downloadRequest: DownloadRequest<T>) {

        val queueAlreadyExists = downloadRepository.hasQueue()

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
                title = downloadRequest.title,
                jobType = downloadRequest.downloadType,
                status = DownloadStatus.QUEUED,
                payload = payload,
                createdAt = now,
                updatedAt = now
            )

            downloadRepository.insertJob(job)
        }

        if (!queueAlreadyExists) {
            startQueue()
        }

    }

    private fun startQueue() {
        if (_isRunning.value) return

        _isRunning.value = true

        managerScope.launch {
            downloadRepository.updateAllStatus(
                status = DownloadStatus.QUEUED
            )

            scheduleNextJob()
        }
    }

    suspend fun pause() {
        _isRunning.value = false
        val job = downloadRepository.getNextJobByStatus(
            DownloadStatus.RUNNING
        )
        if (job != null) {
            val handler = handlerRegistry.get(job.jobType)
            val result = handler.execute(action = DownloadAction.STOP, job = job)
            handleResult(job = job, result)

        }
        downloadRepository.updateAllStatus(
            status = DownloadStatus.PAUSED
        )
    }

    private suspend fun scheduleNextJob() {

        if (!_isRunning.value) return


        val job = downloadRepository.getNextJobByStatus(
            DownloadStatus.QUEUED
        ) ?: run {
            _isRunning.value = false
            return
        }

        downloadRepository.updateStatus(
            jobId = job.jobId,
            status = DownloadStatus.RUNNING
        )


        val handler = handlerRegistry.get(job.jobType)

        val result = handler.execute(action = DownloadAction.START, job = job)

        handleResult(job = job, result)


        if (_isRunning.value) {
            scheduleNextJob()
        }
    }

    suspend fun dispatch(action: DownloadAction) {
        when (action) {
            DownloadAction.START -> {
                startQueue()
            }

            DownloadAction.STOP -> {
                pause()
            }
        }
    }

    private suspend fun handleResult(
        job: DownloadJobEntity,
        result: DownloadResult<DownloadPayload>
    ) {
        when (result) {

            is DownloadResult.Success -> {

                downloadRepository.updateStatus(
                    jobId = job.jobId,
                    status = DownloadStatus.COMPLETED
                )

                downloadDispatcher.emitProgress(
                    DownloadProgressState.Completed(notificationId = result.jobId.hashCode())
                )

                downloadResultRouter.process(jobType = job.jobType, result = result.payload)

//                downloadDispatcher.emitEvent(
//                    DownloadEvents.Completed(
//                        jobId = result.jobId,
//                        jobType = job.jobType,
//                        payload = result.payload
//                    )
//                )

                downloadRepository.deleteJob(job.jobId)

            }

            is DownloadResult.Failure -> {

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

            is DownloadResult.Paused -> {
                downloadRepository.updateStatus(
                    jobId = job.jobId,
                    status = DownloadStatus.PAUSED
                )

                downloadDispatcher.emitProgress(
                    DownloadProgressState.Paused(
                        notificationId = result.jobId.hashCode()
                    )
                )

                downloadDispatcher.emitEvent(
                    DownloadEvents.Paused(
                        jobId = result.jobId,
                        jobType = job.jobType,
                        payload = result.payload
                    )
                )

            }
        }
    }
}