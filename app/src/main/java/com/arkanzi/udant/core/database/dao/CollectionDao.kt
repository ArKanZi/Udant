package com.arkanzi.udant.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.arkanzi.udant.core.database.entity.ArticleEntity
import com.arkanzi.udant.core.database.entity.CollectionEntity
import com.arkanzi.udant.feature.library.model.CollectionWithArticleCount
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
    SELECT *
    FROM articles
    WHERE articleId NOT IN (
        SELECT articleId
        FROM article_collection
    )
    ORDER BY publishedAt DESC
""")
    fun getDefaultArticles(): Flow<List<ArticleEntity>>

    @Query("""
    SELECT
        c.id,
        c.name,
        c.color,
        c.isPinned,
        c.sortOrder,
        c.createdAt,
        c.updatedAt,
        COUNT(ac.savedArticleId) AS articleCount
    FROM collection c
    LEFT JOIN article_collection ac
        ON c.id = ac.collectionId
    GROUP BY c.id
    ORDER BY c.createdAt DESC
""")
    fun getCollectionsWithArticleCount(): Flow<List<CollectionWithArticleCount>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCollection(collection: CollectionEntity): Long

    @Update
    suspend fun updateCollection(collection: CollectionEntity): Int

    @Delete
    suspend fun deleteCollection(collection: CollectionEntity)
}