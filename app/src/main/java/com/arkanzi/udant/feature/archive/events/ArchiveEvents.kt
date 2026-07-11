package com.arkanzi.udant.feature.archive.events

import com.arkanzi.udant.feature.archive.model.ArchiveResponsePayload

sealed interface ArchiveEvent {

    data class Completed(
        val payload: ArchiveResponsePayload
    ) : ArchiveEvent

    data class Failed(
        val jobId: String,
        val throwable: Throwable
    ) : ArchiveEvent
}