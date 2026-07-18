package com.arkanzi.udant.core.mapper

import com.arkanzi.udant.core.database.entity.SavedArticleEntity
import com.arkanzi.udant.core.model.Article

fun SavedArticleEntity.toArticle(): Article {

    return Article(
        articleId = savedArticleId,
        title = title,
        summary = summary,
        author = author,
        imageUrl = imageUrl,
        sourceName = sourceName,
        articleUrl = articleUrl,
        publishedAt = publishedAt,
        category = category,
        savedAt = savedAt,
        archiveStatus = archiveStatus,
        archiveUri = archiveUri
    )
}

fun Article.toSavedArticleEntity(): SavedArticleEntity {

    return SavedArticleEntity(
        title = title,
        summary = summary,
        author = author,
        imageUrl = imageUrl,
        sourceName = sourceName,
        articleUrl = articleUrl,
        publishedAt = publishedAt,
        category = category,
        savedAt = System.currentTimeMillis(),
        archiveStatus = archiveStatus,
        archiveUri = archiveUri
    )
}

fun List<Article>.toSavedArticleEntities(): List<SavedArticleEntity> {

    return map { article ->

        article.toSavedArticleEntity()
    }
}