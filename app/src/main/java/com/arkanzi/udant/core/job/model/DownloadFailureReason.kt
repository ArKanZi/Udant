package com.arkanzi.udant.core.job.model

sealed interface DownloadFailureReason : DownloadJobFailureReason {
    data object PayloadDeserializationFailed : DownloadFailureReason

    data object PayloadSerializationFailed : DownloadFailureReason
}