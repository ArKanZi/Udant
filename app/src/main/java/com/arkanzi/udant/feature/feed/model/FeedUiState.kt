package com.arkanzi.udant.feature.feed.model

import com.arkanzi.udant.core.model.Article

data class FeedUiState(

    val articles: List<Article> = emptyList(),

    val isLoading: Boolean = false,

    val isLoadingMore: Boolean = false,

    val error: String? = null
)