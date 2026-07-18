package com.arkanzi.udant.core.job.download.logging

import com.arkanzi.udant.core.job.download.contract.DownloadFailureReason
import com.arkanzi.udant.core.job.download.model.DownloadType
import kotlin.reflect.KClass

object DownloadSystemLogFormatter {
    fun formatFailure(
        header: String,
        jobId: String?,
        downloadType: DownloadType,
        source: KClass<*>,
        reason: DownloadFailureReason,
        exception: String
    ): String {
        return """
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
$header
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Job Id              : ${jobId ?: "Not Created"}
Download Job Type   : $downloadType
Source              : ${source.java.simpleName}
Reason              : $reason
Exception           : $exception
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
""".trimIndent()

    }
}