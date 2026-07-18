package com.arkanzi.udant.core.job.download.model

import com.arkanzi.udant.core.job.download.contract.DownloadFailureReason

sealed interface DownloadProgressState {

    data class Queued(
        val notificationId: Int
    ) : DownloadProgressState

    data class Loading(
        val notificationId: Int,
        val progress: Int
    ) : DownloadProgressState

    data class Generating(
        val notificationId: Int
    ) : DownloadProgressState
    data class Moving(
        val notificationId: Int
    ) : DownloadProgressState

    data class Completed(
        val notificationId: Int
    ) : DownloadProgressState

    data class Failed(
        val notificationId: Int,
        val reason: DownloadFailureReason
    ) : DownloadProgressState
}