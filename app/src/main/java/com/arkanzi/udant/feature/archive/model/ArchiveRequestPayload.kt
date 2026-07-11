package com.arkanzi.udant.feature.archive.model

import com.arkanzi.udant.core.job.model.DownloadPayload
import kotlinx.serialization.Serializable

@Serializable
data class ArchiveRequestPayload(
    val articleTitle: String,
    val articleUrl: String

) : DownloadPayload
