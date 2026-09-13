package com.arkanzi.udant.feature.feed.model

import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.core.model.FeedCategory

data class FeedUiState(
    val categories: List<FeedCategory> = emptyList(),

    val articles: List<Article> = emptyList(),

    val isLoading: Boolean = false,

    val isLoadingMore: Boolean = false,

    val error: String? = null
)