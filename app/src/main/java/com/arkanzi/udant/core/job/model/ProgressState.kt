package com.arkanzi.udant.core.job.model

sealed interface ProgressState {

    data class Queued(
        val notificationId: Int
    ) : ProgressState

    data class Loading(
        val notificationId: Int,
        val progress: Int
    ) : ProgressState

    data class Generating(
        val notificationId: Int
    ) : ProgressState
    data class Moving(
        val notificationId: Int
    ) : ProgressState

    data class Completed(
        val notificationId: Int
    ) : ProgressState

    data class Failed(
        val notificationId: Int,
        val reason: DownloadJobFailureReason
    ) : ProgressState
}