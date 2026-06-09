package com.arkanzi.udant.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.arkanzi.udant.core.database.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Query(""" SELECT * FROM articles ORDER BY publishedAt DESC """)
    fun getAllArticles(): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticles(
        articles: List<ArticleEntity>
    )

    @Query(""" DELETE FROM articles """)
    suspend fun deleteArticles()

    @Transaction
    suspend fun replaceArticles(
        articles: List<ArticleEntity>
    ) {
        deleteArticles()
        insertArticles(articles)
    }

    @Query("""
    UPDATE articles
    SET imageUrl = :imageUrl,
        summary = :summary
    WHERE articleUrl = :articleUrl
""")
    suspend fun updateEnrichment(
        articleUrl: String,
        imageUrl: String?,
        summary: String
    )
}