package com.arkanzi.udant.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arkanzi.udant.core.job.model.DownloadJobStatus
import com.arkanzi.udant.core.job.model.DownloadJobType

@Entity(tableName = "download_jobs")
data class DownloadJobEntity(

    @PrimaryKey
    val jobId: String,

    val referenceId: Long,

    val jobType: DownloadJobType,

    val status: DownloadJobStatus,

    val payload: String,

    val createdAt: Long,

    val updatedAt: Long,

    val retryCount: Int = 0
)