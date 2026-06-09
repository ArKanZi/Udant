package com.arkanzi.udant.feature.saved.repository

import com.arkanzi.udant.core.database.dao.SavedArticleDao
import com.arkanzi.udant.core.mapper.toArticle
import com.arkanzi.udant.core.mapper.toSavedArticleEntity
import com.arkanzi.udant.core.model.Article
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedArticleRepository @Inject constructor(

    private val savedArticleDao: SavedArticleDao

) {

    fun getSavedArticles(): Flow<List<Article>> {

        return savedArticleDao
            .getAllSavedArticles()
            .map { entities ->

                entities.map { entity ->

                    entity.toArticle()
                }
            }
    }

    suspend fun saveArticle(
        article: Article
    ) {

        savedArticleDao.insertArticle(
            article.toSavedArticleEntity()
        )
    }

    suspend fun removeSavedArticle(
        articleUrl: String
    ) {

        savedArticleDao.deleteArticleByUrl(
            articleUrl = articleUrl
        )
    }

    fun getSavedUrls(): Flow<Set<String>> {

        return savedArticleDao
            .getSavedUrls()
            .map { urls ->

                urls.toSet()
            }
    }
}