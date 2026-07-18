package com.arkanzi.udant.core.job.model

import com.arkanzi.udant.core.job.contract.JobPayload

sealed interface JobRequest<T : JobPayload> {

    val jobType: JobType
    val referenceId:Long
    val payload: T

    data class Execute<T : JobPayload>(
        override val jobType: JobType,
        override val referenceId: Long,
        override val payload: T
    ) : JobRequest<T>
}