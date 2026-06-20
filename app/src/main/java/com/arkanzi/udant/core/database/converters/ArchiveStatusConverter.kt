package com.arkanzi.udant.core.database.converters

import androidx.room.TypeConverter
import com.arkanzi.udant.core.model.ArchiveStatus

class ArchiveStatusConverter {

    @TypeConverter
    fun fromArchiveStatus(
        status: ArchiveStatus
    ): String {

        return status.name
    }

    @TypeConverter
    fun toArchiveStatus(
        value: String
    ): ArchiveStatus {

        return ArchiveStatus.valueOf(value)
    }
}