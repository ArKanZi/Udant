package com.arkanzi.udant.feature.archive.model

import com.arkanzi.udant.core.job.download.contract.DownloadPayload

data class ArchiveResponsePayload(
    val savedArticleId: Long,
    val archiveUri: String
): DownloadPayload