package com.arkanzi.udant.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arkanzi.udant.core.database.entity.SavedArticleEntity
import com.arkanzi.udant.core.model.ArchiveStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedArticleDao {

    @Query("""
        SELECT *
        FROM saved_articles
        ORDER BY savedAt DESC
    """)
    fun getAllSavedArticles(): Flow<List<SavedArticleEntity>>

    @Query("""
        SELECT saved_articles.*
        FROM saved_articles
        INNER JOIN article_collection
            ON saved_articles.savedArticleId = article_collection.savedArticleId
        WHERE article_collection.collectionId = :collectionId
        ORDER BY saved_articles.savedAt DESC
    """)
    fun getSavedArticlesInCollection(
        collectionId: String
    ): Flow<List<SavedArticleEntity>>

    @Query("""
        SELECT *
        FROM saved_articles
        WHERE savedArticleId NOT IN (
            SELECT savedArticleId
            FROM article_collection
        )
        ORDER BY savedAt DESC
    """)
    fun getDefaultSavedArticles(): Flow<List<SavedArticleEntity>>

    @Query("""
        SELECT *
        FROM saved_articles
        WHERE savedArticleId = :savedArticleId
    """)
    suspend fun getSavedArticleById(
        savedArticleId: Long
    ): SavedArticleEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticle(
        article: SavedArticleEntity
    )

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticles(
        articles: List<SavedArticleEntity>
    )

    @Query("""
        DELETE FROM saved_articles
        WHERE articleUrl = :articleUrl
    """)
    suspend fun deleteArticleByUrl(
        articleUrl: String
    )

    @Query("""
        DELETE FROM saved_articles
    """)
    suspend fun deleteAllArticles()

    @Query("""
        SELECT COUNT(*)
        FROM saved_articles
    """)
    fun getSavedCount(): Flow<Int>

    @Query("""
        SELECT articleUrl
        FROM saved_articles
    """)
    fun getSavedUrls(): Flow<List<String>>

    @Query("""
        UPDATE saved_articles
        SET archiveStatus = :archiveStatus
        WHERE savedArticleId = :savedArticleId
    """)
    suspend fun updateArchiveStatus(
        savedArticleId: Long,
        archiveStatus: ArchiveStatus
    )

    @Query("""
        UPDATE saved_articles
        SET archiveStatus = "COMPLETED",
            archiveUri = :archiveUri
        WHERE savedArticleId = :savedArticleId
    """)
    suspend fun updateArchiveCompleted(
        savedArticleId: Long,
        archiveUri: String
    )

    @Query("""
        UPDATE saved_articles
        SET archiveStatus = "NOT_ARCHIVED",
            archiveUri = null
        WHERE savedArticleId = :savedArticleId
    """)
    suspend fun clearArchive(
        savedArticleId: Long
    )

    @Query("""
        UPDATE saved_articles
        SET archiveUri = :archiveUri,
            archiveStatus = :archiveStatus
        WHERE savedArticleId = :savedArticleId
    """)
    suspend fun updateArchive(
        savedArticleId: Long,
        archiveUri: String?,
        archiveStatus: ArchiveStatus
    )
}