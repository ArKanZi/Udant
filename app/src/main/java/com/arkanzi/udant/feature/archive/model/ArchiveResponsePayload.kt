package com.arkanzi.udant.feature.archive.model

import com.arkanzi.udant.core.job.model.DownloadPayload

data class ArchiveResponsePayload(
    val savedArticleId: Long,
    val archiveUri: String
): DownloadPayload