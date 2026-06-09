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
        savedAt = savedAt
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
        savedAt = System.currentTimeMillis()
    )
}

fun List<Article>.toSavedArticleEntities(): List<SavedArticleEntity> {

    return map { article ->

        article.toSavedArticleEntity()
    }
}