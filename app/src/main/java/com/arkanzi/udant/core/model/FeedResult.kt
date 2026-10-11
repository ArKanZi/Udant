package com.arkanzi.udant.core.model

data class FeedResult(
    val articles: List<Article>,
    val hasNextPage: Boolean
)