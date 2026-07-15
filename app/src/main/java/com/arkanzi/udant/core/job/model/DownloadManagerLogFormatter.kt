package com.arkanzi.udant.core.job.model

import kotlin.reflect.KClass

object DownloadManagerLogFormatter {
    fun formatFailure(
        header: String,
        jobId: String?,
        downloadJobType: DownloadJobType,
        source: KClass<*>,
        reason: DownloadJobFailureReason,
        exception: String
    ): String {
        return """
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
$header
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Job Id              : ${jobId ?: "Not Created"}
Download Job Type   : $downloadJobType
Source              : ${source.java.simpleName}
Reason              : $reason
Exception           : $exception
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
""".trimIndent()

    }
}