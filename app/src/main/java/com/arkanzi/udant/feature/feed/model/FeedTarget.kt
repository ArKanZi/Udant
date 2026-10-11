package com.arkanzi.udant.feature.feed.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface FeedTarget {
    @Serializable
    data object All : FeedTarget
    @Serializable
    data class Category(
        val categoryId: String
    ) : FeedTarget
}