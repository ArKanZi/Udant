package com.arkanzi.udant.core.job.codec

import com.arkanzi.udant.core.job.model.DownloadJobType
import com.arkanzi.udant.core.job.model.DownloadPayload

interface PayloadCodec {

    val jobType: DownloadJobType

    fun serialize(payload: DownloadPayload): String

    fun deserialize(payload: String): DownloadPayload
}