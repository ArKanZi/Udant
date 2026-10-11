package com.arkanzi.udant.feature.library.repository

import androidx.room.withTransaction
import com.arkanzi.udant.core.database.UdantDatabase
import com.arkanzi.udant.core.database.dao.ArticleCollectionDao
import com.arkanzi.udant.core.database.dao.SavedArticleDao
import com.arkanzi.udant.core.database.entity.ArticleCollectionCrossRef
import com.arkanzi.udant.core.database.entity.SavedArticleEntity
import com.arkanzi.udant.core.mapper.toModel
import com.arkanzi.udant.core.model.ArchiveStatus
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.feature.library.model.LibraryCollectionTarget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedArticleRepository @Inject constructor(

    private val savedArticleDao: SavedArticleDao,
    private val articleCollectionDao: ArticleCollectionDao,
    private val collectionDao: ArticleCollectionDao,
    private val database: UdantDatabase

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

    suspend fun insertArticle(
        savedArticle: SavedArticleEntity
    ) {

        savedArticleDao.insertArticle(
            savedArticle
        )
    }

    suspend fun insertSavedArticle(
        article: SavedArticleEntity,
        collectionId: String?
    ){
        database.withTransaction {
            savedArticleDao.insertArticle(article)

            if (collectionId != null) {
                articleCollectionDao.insert(
                    ArticleCollectionCrossRef(
                        savedArticleId = article.savedArticleId,
                        collectionId = collectionId
                    )
                )
                collectionDao.incrementArticleCount(collectionId)
            }

        }

    }

    suspend fun deleteSavedArticleFromAllCollection(article: Article) {
        database.withTransaction {

            val collectionIds =
                articleCollectionDao
                    .getCollectionIdsBySavedArticleId(article.articleId)

            collectionIds.forEach { collectionId ->

                articleCollectionDao.removeFromCollection(
                    savedArticleId = article.articleId,
                    collectionId = collectionId
                )

                collectionDao.decrementArticleCount(collectionId)
            }

            savedArticleDao.deleteArticleById(article.articleId)
        }
    }

    suspend fun deleteSavedArticleFromCollection(article: Article,collectionId: String) {
        database.withTransaction {
                articleCollectionDao.removeFromCollection(
                    savedArticleId = article.articleId,
                    collectionId = collectionId
                )

                collectionDao.decrementArticleCount(collectionId)
        }
    }

    suspend fun updateCollections(
        articleId: String,
        collectionIds: List<String>
    ) {
        database.withTransaction {

            val oldCollectionIds =
                articleCollectionDao
                    .getCollectionIdsBySavedArticleId(articleId)
                    .toSet()

            val newCollectionIds =
                collectionIds.toSet()

            oldCollectionIds
                .subtract(newCollectionIds)
                .forEach { collectionId ->

                    articleCollectionDao.removeFromCollection(
                        savedArticleId = articleId,
                        collectionId = collectionId
                    )

                    collectionDao.decrementArticleCount(
                        collectionId
                    )
                }

            newCollectionIds
                .subtract(oldCollectionIds)
                .forEach { collectionId ->

                    articleCollectionDao.insert(
                        ArticleCollectionCrossRef(
                            savedArticleId = articleId,
                            collectionId = collectionId
                        )
                    )

                    collectionDao.incrementArticleCount(
                        collectionId
                    )
                }
        }
    }

    fun getSavedUrls(): Flow<Set<String>> {

        return savedArticleDao
            .getSavedUrls()
            .map { urls ->
                urls.toSet()
            }
    }

    suspend fun updateArchive(
        savedArticleId: String,
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