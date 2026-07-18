package com.arkanzi.udant.feature.archive.codec

import com.arkanzi.udant.core.job.download.codec.DownloadPayloadCodec
import com.arkanzi.udant.core.job.download.model.DownloadType
import com.arkanzi.udant.core.job.download.contract.DownloadPayload
import com.arkanzi.udant.feature.archive.model.ArchiveRequestPayload
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchivePayloadCodec @Inject constructor() : DownloadPayloadCodec {

    override val jobType =
        DownloadType.ARCHIVE

    override fun serialize(
        payload: DownloadPayload
    ): String {

        return Json.encodeToString(
            payload as ArchiveRequestPayload
        )
    }

    override fun deserialize(
        payload: String
    ): DownloadPayload {

        return Json.decodeFromString<ArchiveRequestPayload>(
            payload
        )
    }
}