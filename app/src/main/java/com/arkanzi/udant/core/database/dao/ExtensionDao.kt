package com.arkanzi.udant.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.arkanzi.udant.core.database.entity.ExtensionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExtensionDao {
    @Query("SELECT * FROM extensions")
    fun getExtensions(): Flow<List<ExtensionEntity>>

    @Upsert
    suspend fun upsertExtensions(
        extensions: List<ExtensionEntity>
    )
}