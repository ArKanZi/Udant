package com.arkanzi.udant.core.job.download.model

import com.arkanzi.udant.core.job.download.contract.DownloadFailureReason

sealed interface DownloadManagerFailureReason : DownloadFailureReason {
    data object PayloadDeserializationFailed : DownloadManagerFailureReason

    data object PayloadSerializationFailed : DownloadManagerFailureReason
}