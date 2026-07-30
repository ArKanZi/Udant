package com.arkanzi.udant.core.job.download.model

enum class DownloadStatus {
    QUEUED,
    RUNNING,
    COPYING,
    COMPLETED,
    CLEANING,

    PAUSED,
    FAILED,
    CANCELLED
}