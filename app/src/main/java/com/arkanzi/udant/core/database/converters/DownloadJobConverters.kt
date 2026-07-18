package com.arkanzi.udant.core.database.converters

import androidx.room.TypeConverter
import com.arkanzi.udant.core.job.download.model.DownloadStatus
import com.arkanzi.udant.core.job.download.model.DownloadType

class DownloadJobConverters {

    @TypeConverter
    fun fromDownloadJobStatus(
        status: DownloadStatus
    ): String = status.name

    @TypeConverter
    fun toDownloadJobStatus(
        value: String
    ): DownloadStatus = DownloadStatus.valueOf(value)

    @TypeConverter
    fun fromDownloadJobType(
        type: DownloadType
    ): String = type.name

    @TypeConverter
    fun toDownloadJobType(
        value: String
    ): DownloadType = DownloadType.valueOf(value)
}