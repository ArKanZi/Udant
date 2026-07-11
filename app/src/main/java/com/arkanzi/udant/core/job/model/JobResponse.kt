package com.arkanzi.udant.core.job.model

sealed interface JobResponse {

    data class Completed(
        val jobId: String,
        val jobType: JobType,
        val payload: JobPayload
    ) : JobResponse

    data class Failed(
        val jobId: String,
        val jobType: JobType,
        val throwable: Throwable
    ) : JobResponse
}