package com.arkanzi.udant.feature.archive.model

import com.arkanzi.udant.core.job.download.contract.DownloadPayload

data class ArchiveResultPayload(
    val savedArticleId: String,
    val archiveUri: String?=null
): DownloadPayload