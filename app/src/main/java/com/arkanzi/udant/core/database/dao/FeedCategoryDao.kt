package com.arkanzi.udant.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.arkanzi.udant.core.database.entity.FeedCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedCategoryDao {

    @Query("SELECT * FROM feed_categories")
    fun getCategories(): Flow<List<FeedCategoryEntity>>

    @Upsert
    suspend fun upsertCategories(
        categories: List<FeedCategoryEntity>
    )

    @Insert
    suspend fun insertCategories(
        categories: List<FeedCategoryEntity>
    )


}