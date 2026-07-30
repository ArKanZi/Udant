package com.arkanzi.udant.feature.archive.events

import com.arkanzi.udant.feature.archive.model.ArchiveResultPayload

sealed interface ArchiveEvent {

    data class Completed(
        val payload: ArchiveResultPayload
    ) : ArchiveEvent

    data class Failed(
        val jobId: String,
        val throwable: Throwable
    ) : ArchiveEvent
}