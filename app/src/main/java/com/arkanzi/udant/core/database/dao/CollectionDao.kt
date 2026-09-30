package com.arkanzi.udant.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.arkanzi.udant.core.database.entity.CollectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {

    @Query("SELECT * FROM collection ORDER BY createdAt DESC")
    fun getAllCollections(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collection WHERE id = :id")
    suspend fun getCollection(id: Long): CollectionEntity?

    @Query("""
    SELECT EXISTS(
        SELECT 1
        FROM collection
        WHERE name = :name
    )
""")
    suspend fun existsByName(name: String): Boolean

    @Query("""
    SELECT COUNT(*)
    FROM saved_articles sa
    WHERE NOT EXISTS (
    SELECT 1
    FROM article_collection ac
    WHERE ac.savedArticleId = sa.savedArticleId
)
""")
    fun getDefaultArticleCount(): Flow<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCollection(collection: CollectionEntity): Long

    @Update
    suspend fun updateCollection(collection: CollectionEntity): Int

    @Delete
    suspend fun deleteCollection(collection: CollectionEntity): Int
}