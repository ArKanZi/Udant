package com.arkanzi.udant.core.job.registry

import com.arkanzi.udant.core.job.codec.PayloadCodec
import com.arkanzi.udant.core.job.model.DownloadJobType
import com.arkanzi.udant.feature.archive.codec.ArchiveRequestPayloadCodec
import javax.inject.Inject

class PayloadCodecRegistry @Inject constructor(

    archiveCodec: ArchiveRequestPayloadCodec

) {

    private val codecs = mapOf(
        archiveCodec.jobType to archiveCodec
    )

    fun get(
        jobType: DownloadJobType
    ): PayloadCodec =
        checkNotNull(codecs[jobType])
}