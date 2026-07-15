package com.arkanzi.udant.feature.archive.model

import com.arkanzi.udant.core.job.model.DownloadJobFailureReason

sealed interface ArchiveFailureReason: DownloadJobFailureReason {

    sealed interface ArchiveService : ArchiveFailureReason {
        data object WebViewCreationFailed : ArchiveService
        data object StoragePathNotFound: ArchiveService
        data object PageLoadStartFailed: ArchiveService
        data object PageLoadFailed : ArchiveService
        data object SaveWebArchiveFailed : ArchiveService
        data object HttpError: ArchiveService
    }

    sealed interface ArchiveJob : ArchiveFailureReason {

        data object ServiceStartFailed: ArchiveJob

        data object MoveToSafFailed : ArchiveJob

    }
}