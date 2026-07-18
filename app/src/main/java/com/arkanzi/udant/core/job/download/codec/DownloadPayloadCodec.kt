package com.arkanzi.udant.core.job.download.codec

import com.arkanzi.udant.core.job.download.model.DownloadType
import com.arkanzi.udant.core.job.download.contract.DownloadPayload

interface DownloadPayloadCodec {

    val jobType: DownloadType

    fun serialize(payload: DownloadPayload): String

    fun deserialize(payload: String): DownloadPayload
}