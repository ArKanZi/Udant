package com.arkanzi.udant.feature.library.usecase.savedArticles

import com.arkanzi.udant.feature.library.repository.SavedArticleRepository
import javax.inject.Inject

class UpdateCollectionsUseCase @Inject constructor(
    private val repository: SavedArticleRepository
) {
    suspend operator fun invoke(articleId: String,
                                collectionIds: List<String>) {

        repository.updateCollections(
            articleId = articleId,
            collectionIds = collectionIds
        )
    }
}