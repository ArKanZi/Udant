package com.arkanzi.udant.feature.archive.events

sealed interface ArchiveProgressEvent {

    data class Queued(
        val jobId: String
    ) : ArchiveProgressEvent

    data class Loading(
        val jobId: String,
        val progress: Int
    ) : ArchiveProgressEvent

    data class GeneratingArchive(
        val jobId: String
    ) : ArchiveProgressEvent

    data class CopyingToSaf(
        val jobId: String
    ) : ArchiveProgressEvent

    data class CleaningUp(
        val jobId: String
    ) : ArchiveProgressEvent

    data class Completed(
        val jobId: String
    ) : ArchiveProgressEvent

    data class Failed(
        val jobId: String,
        val throwable: Throwable
    ) : ArchiveProgressEvent
}