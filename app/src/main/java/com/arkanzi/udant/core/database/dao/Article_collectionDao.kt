package com.arkanzi.udant.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arkanzi.udant.core.database.entity.ArticleCollectionCrossRef
import com.arkanzi.udant.core.database.entity.SavedArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleCollectionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(
        articleCollection: ArticleCollectionCrossRef
    )

    @Delete
    suspend fun delete(
        articleCollection: ArticleCollectionCrossRef
    )

    @Query("""
        DELETE FROM article_collection
        WHERE savedArticleId = :savedArticleId
        AND collectionId = :collectionId
    """)
    suspend fun removeFromCollection(
        savedArticleId: Long,
        collectionId: Long
    )

    @Query("""
        UPDATE article_collection
        SET isPinned = :isPinned
        WHERE savedArticleId = :savedArticleId
        AND collectionId = :collectionId
    """)
    suspend fun setPinned(
        savedArticleId: Long,
        collectionId: Long,
        isPinned: Boolean
    )

    @Query("""
        SELECT isPinned
        FROM article_collection
        WHERE savedArticleId = :savedArticleId
        AND collectionId = :collectionId
    """)
    suspend fun isPinned(
        savedArticleId: Long,
        collectionId: Long
    ): Boolean?

    @Query("""
    SELECT saved_articles.*
    FROM saved_articles
    INNER JOIN article_collection
        ON saved_articles.savedArticleId = article_collection.savedArticleId
    WHERE article_collection.collectionId = :collectionId
    ORDER BY saved_articles.publishedAt DESC
""")
    fun getArticlesInCollection(
        collectionId: Long
    ): Flow<List<SavedArticleEntity>>

    @Query("""
    SELECT *
    FROM saved_articles
    WHERE savedArticleId NOT IN (
        SELECT savedArticleId
        FROM article_collection
    )
    ORDER BY publishedAt DESC
""")
    fun getDefaultArticles(): Flow<List<SavedArticleEntity>>

    @Query("""
    SELECT COUNT(*)
    FROM article_collection
    WHERE collectionId = :collectionId
""")
    fun getArticleCount(
        collectionId: Long
    ): Flow<Int>

    @Query("""
    SELECT COUNT(*)
    FROM saved_articles
    WHERE savedArticleId NOT IN (
        SELECT savedArticleId
        FROM article_collection
    )
""")
    fun getDefaultArticleCount(): Flow<Int>


}