package com.arkanzi.udant.core.job

import android.util.Log
import com.arkanzi.udant.core.database.entity.DownloadJobEntity
import com.arkanzi.udant.core.job.event.DownloadEvent
import com.arkanzi.udant.core.job.model.DownloadJobResponse
import com.arkanzi.udant.core.job.model.DownloadJobStatus
import com.arkanzi.udant.core.job.model.DownloadPayload
import com.arkanzi.udant.core.job.model.DownloadRequest
import com.arkanzi.udant.core.job.registry.DownloadJobHandlerRegistry
import com.arkanzi.udant.core.job.registry.PayloadCodecRegistry
import com.arkanzi.udant.core.job.repository.DownloadJobRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadManager @Inject constructor(
    private val downloadJobRepository: DownloadJobRepository,
    private val handlerRegistry: DownloadJobHandlerRegistry,
    private val payloadCodecRegistry: PayloadCodecRegistry

) {

    private val _downloadEvents = MutableSharedFlow<DownloadEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )

    val downloadEvents: SharedFlow<DownloadEvent> =
        _downloadEvents

    private val managerScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )
    private var isRunning = false

    suspend fun <T : DownloadPayload> enqueue(downloadRequest: DownloadRequest<T>) {

        val codec = payloadCodecRegistry.get(downloadRequest.downloadJobType)

        val payload = codec.serialize(downloadRequest.payload)

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

        Log.d("DownloadManager", "Before handler.execute()")
        val result = handler.execute(job)
        Log.d("DownloadManager", "After handler.execute()")

        when (result) {

            is DownloadJobResponse.Success -> {

                downloadJobRepository.updateStatus(
                    jobId = job.jobId,
                    status = DownloadJobStatus.COMPLETED
                )

                _downloadEvents.emit(
                    DownloadEvent.Completed(
                        jobId = result.jobId,
                        jobType = job.jobType,
                        payload = result.payload
                    )
                )
            }

            is DownloadJobResponse.Failure -> {

                downloadJobRepository.updateStatus(
                    jobId = job.jobId,
                    status = DownloadJobStatus.FAILED
                )

                _downloadEvents.emit(
                    DownloadEvent.Failed(
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