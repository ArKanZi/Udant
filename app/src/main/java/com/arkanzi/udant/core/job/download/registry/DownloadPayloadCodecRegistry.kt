package com.arkanzi.udant.core.job.download.registry

import com.arkanzi.udant.core.job.download.codec.DownloadPayloadCodec
import com.arkanzi.udant.core.job.download.model.DownloadType
import com.arkanzi.udant.feature.archive.codec.ArchivePayloadCodec
import javax.inject.Inject

class DownloadPayloadCodecRegistry @Inject constructor(

    archiveCodec: ArchivePayloadCodec

) {

    private val codecs = mapOf(
        archiveCodec.jobType to archiveCodec
    )

    fun get(
        jobType: DownloadType
    ): DownloadPayloadCodec =
        checkNotNull(codecs[jobType])
}