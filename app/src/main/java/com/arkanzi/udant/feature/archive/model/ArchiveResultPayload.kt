package com.arkanzi.udant.feature.archive.model

import com.arkanzi.udant.core.job.download.contract.DownloadPayload

data class ArchiveResultPayload(
    val savedArticleId: Long,
    val archiveUri: String?=null
): DownloadPayload