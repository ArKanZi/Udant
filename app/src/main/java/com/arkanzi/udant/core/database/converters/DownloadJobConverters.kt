package com.arkanzi.udant.core.database.converter

import androidx.room.TypeConverter
import com.arkanzi.udant.core.job.model.DownloadJobStatus
import com.arkanzi.udant.core.job.model.DownloadJobType

class DownloadJobConverters {

    @TypeConverter
    fun fromDownloadJobStatus(
        status: DownloadJobStatus
    ): String = status.name

    @TypeConverter
    fun toDownloadJobStatus(
        value: String
    ): DownloadJobStatus = DownloadJobStatus.valueOf(value)

    @TypeConverter
    fun fromDownloadJobType(
        type: DownloadJobType
    ): String = type.name

    @TypeConverter
    fun toDownloadJobType(
        value: String
    ): DownloadJobType = DownloadJobType.valueOf(value)
}