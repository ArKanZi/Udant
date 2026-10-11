package com.arkanzi.udant.feature.library.usecase.savedArticles

import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.feature.library.repository.SavedArticleRepository
import javax.inject.Inject

class DeleteSavedArticleUseCase @Inject constructor(
    private val repository: SavedArticleRepository
) {
    suspend fun deleteSavedArticleFromAllCollection(article: Article) {

        repository.deleteSavedArticleFromAllCollection(
            article = article
        )
    }

    suspend fun deleteSavedArticleFromCollection(article: Article,collectionId: String) {
        repository.deleteSavedArticleFromCollection(
            article = article,
            collectionId = collectionId)
    }
}