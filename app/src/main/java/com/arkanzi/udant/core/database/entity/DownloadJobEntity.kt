package com.arkanzi.udant.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arkanzi.udant.core.job.download.model.DownloadStatus
import com.arkanzi.udant.core.job.download.model.DownloadType

@Entity(tableName = "download_jobs")
data class DownloadJobEntity(

    @PrimaryKey
    val jobId: String,

    val referenceId: Long,

    val jobType: DownloadType,

    val status: DownloadStatus,

    val payload: String,

    val createdAt: Long,

    val updatedAt: Long,

    val retryCount: Int = 0
)