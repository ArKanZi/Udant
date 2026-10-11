package com.arkanzi.udant.feature.library.usecase.savedArticles

import com.arkanzi.udant.core.database.dao.ArticleCollectionDao
import com.arkanzi.udant.core.model.Article
import javax.inject.Inject

class SavedArticleApplicationService @Inject constructor(
    private val insertSavedArticleUseCase: InsertSavedArticleUseCase,
    private val deleteSavedArticleUseCase: DeleteSavedArticleUseCase,
    private val updateCollectionsUseCase: UpdateCollectionsUseCase,
    private val articleCollectionDao: ArticleCollectionDao
) {
    suspend fun getCollectionIds(
        articleId: String
    ): List<String> {
        return articleCollectionDao
            .getCollectionIdsBySavedArticleId(articleId)
    }

    suspend fun insertArticle(article: Article) {
        insertSavedArticleUseCase(article)
    }

    suspend fun updateCollections(
        articleId: String,
        collectionIds: List<String>
    ) {
        updateCollectionsUseCase(articleId, collectionIds)
    }

    suspend fun deleteSavedArticleFromAllCollection(article: Article) {
        deleteSavedArticleUseCase.deleteSavedArticleFromAllCollection(article)
    }

    suspend fun deleteSavedArticleFromCollection(
        article: Article,
        collectionId: String,
    ) {
        deleteSavedArticleUseCase.deleteSavedArticleFromCollection(article, collectionId)
    }

}