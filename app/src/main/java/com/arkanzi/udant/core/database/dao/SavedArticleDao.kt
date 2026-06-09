package com.arkanzi.udant.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arkanzi.udant.core.database.entity.SavedArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedArticleDao {

    @Query("""
        SELECT * 
        FROM saved_articles 
        ORDER BY savedAt DESC
    """)
    fun getAllSavedArticles(): Flow<List<SavedArticleEntity>>

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
    suspend fun getSavedCount(): Int

    @Query("""
    SELECT articleUrl
    FROM saved_articles
""")
    fun getSavedUrls(): Flow<List<String>>
}

