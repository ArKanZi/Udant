package com.arkanzi.udant.feature.archive.model

import com.arkanzi.udant.core.job.download.contract.DownloadFailureReason

sealed interface ArchiveFailureReason: DownloadFailureReason {

    sealed interface ArchiveService : ArchiveFailureReason {
        data object WebViewCreationFailed : ArchiveService
        data object StoragePathNotFound: ArchiveService
        data object PageLoadStartFailed: ArchiveService
        data object PageLoadFailed : ArchiveService
        data object SaveWebArchiveFailed : ArchiveService
        data object HttpError: ArchiveService
    }

    sealed interface Archive : ArchiveFailureReason {
        data object ServiceStartFailed: Archive
        data object MoveToSafFailed : Archive

    }
}