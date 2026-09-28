package com.arkanzi.udant.feature.library.repository

import com.arkanzi.udant.core.database.dao.SavedArticleDao
import com.arkanzi.udant.core.mapper.toModel
import com.arkanzi.udant.core.mapper.toSavedArticleEntity
import com.arkanzi.udant.core.model.ArchiveStatus
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.feature.library.model.LibraryCollectionTarget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedArticleRepository @Inject constructor(

    private val savedArticleDao: SavedArticleDao

) {

    fun getSavedArticles(
        target: LibraryCollectionTarget
    ): Flow<List<Article>> {

        val articles = when (target) {

            LibraryCollectionTarget.Default ->
                savedArticleDao.getDefaultSavedArticles()

            is LibraryCollectionTarget.User ->
                savedArticleDao.getSavedArticlesInCollection(
                    collectionId = target.collectionId
                )
        }

        return articles.map { entities ->

            entities.map { entity ->
                entity.toModel()
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

    suspend fun updateArchive(
        savedArticleId: Long,
        archiveUri: String?,
        archiveStatus: ArchiveStatus
    ) {

        savedArticleDao.updateArchive(
            savedArticleId = savedArticleId,
            archiveUri = archiveUri,
            archiveStatus = archiveStatus
        )
    }
}