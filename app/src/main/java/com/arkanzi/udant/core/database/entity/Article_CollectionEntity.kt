package com.arkanzi.udant.core.database.entity

import androidx.room.Entity

@Entity(
    tableName = "article_collection",
    primaryKeys = ["savedArticleId", "collectionId"]
)
data class ArticleCollectionCrossRef(
    val savedArticleId: Long,
    val collectionId: String,
    val isPinned: Boolean = false
)