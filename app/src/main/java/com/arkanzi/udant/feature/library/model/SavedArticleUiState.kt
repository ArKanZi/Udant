package com.arkanzi.udant.feature.library.model

import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.core.model.CollectionModel
import com.arkanzi.udant.feature.library.viewmodel.SavedArticleOrder
import com.arkanzi.udant.feature.library.viewmodel.SavedArticleSort

data class SavedArticleUiState(
    val articles: List<Article> = emptyList(),

    val sortBy: SavedArticleSort = SavedArticleSort.SAVED_DATE,
    val orderBy: SavedArticleOrder = SavedArticleOrder.NEWEST_FIRST,

    val isLoading: Boolean = false,
    val error: String? = null,

    val showDeleteDialog: Boolean = false,
    val collectionCountToDelete: Int?=null,
    val collectionIdToDelete: String?=null,
    val articleToDelete: Article? = null,

    val collections: List<CollectionModel> = emptyList(),

    val showCollectionDialog: Boolean = false,
    val collectionDialogArticle: Article? = null,
    val selectedCollectionIds: Set<String> = emptySet(),
    val isUncategorized: Boolean = false
)