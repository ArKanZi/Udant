package com.arkanzi.udant.feature.archive.codec

import com.arkanzi.udant.core.job.codec.PayloadCodec
import com.arkanzi.udant.core.job.model.DownloadJobType
import com.arkanzi.udant.core.job.model.DownloadPayload
import com.arkanzi.udant.feature.archive.model.ArchiveRequestPayload
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveRequestPayloadCodec @Inject constructor() : PayloadCodec {

    override val jobType =
        DownloadJobType.ARCHIVE

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