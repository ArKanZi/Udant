package com.arkanzi.udant.feature.feed.model

import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.core.model.CollectionModel
import com.arkanzi.udant.core.model.FeedCategory

data class FeedUiState(
    val categories: List<FeedCategory> = emptyList(),
    val articles: List<Article> = emptyList(),

    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,

    val showDeleteDialog: Boolean = false,
    val articleToDelete: Article? = null,

    val collections: List<CollectionModel> = emptyList(),

    val showCollectionDialog: Boolean = false,
    val collectionDialogArticle: Article? = null,
    val selectedCollectionIds: Set<String> = emptySet(),
    val isUncategorized: Boolean = false
)