package com.arkanzi.udant.feature.library.model

data class CollectionWithArticleCount(
    val id: Long,
    val name: String,
    val color: Long,
    val isPinned: Boolean,
    val sortOrder: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val articleCount: Int
)