package com.arkanzi.udant.feature.library.usecase.savedArticles

import com.arkanzi.udant.core.mapper.toSavedArticleEntity
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.core.preference.AppPreferenceRepository
import com.arkanzi.udant.feature.library.repository.SavedArticleRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class InsertSavedArticleUseCase @Inject constructor(
    private val repository: SavedArticleRepository,
    private val appPreferenceRepository: AppPreferenceRepository
) {
    suspend operator fun invoke(article: Article) {

        val collectionId =
            appPreferenceRepository.getDefaultSaveCollectionId().first()

        repository.insertSavedArticle(
            article = article.toSavedArticleEntity(),
            collectionId = collectionId
        )
    }
}