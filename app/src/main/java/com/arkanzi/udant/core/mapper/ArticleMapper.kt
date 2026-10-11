package com.arkanzi.udant.core.mapper

import com.arkanzi.udant.core.database.entity.ArticleEntity
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.core.util.generateArticleId

fun ArticleEntity.toModel(): Article {

    return Article(
        articleId = articleId,
        title = title,
        summary = summary,
        author = author,
        imageUrl = imageUrl,
        sourceId = sourceId,
        sourceName = sourceName,
        articleUrl = articleUrl,
        publishedAt = publishedAt,
        category = category
    )
}

fun Article.toEntity(extensionId: String): ArticleEntity {

    return ArticleEntity(
        articleId = generateArticleId(
            sourceId = extensionId,
            articleUrl = articleUrl
        ),
        title = title,
        summary = summary,
        author = author,
        imageUrl = imageUrl,
        sourceId = extensionId,
        sourceName = sourceName,
        articleUrl = articleUrl,
        publishedAt = publishedAt,
        category = category
    )
}

fun List<Article>.toArticleEntities(extensionId: String): List<ArticleEntity> {

    return map { article ->

        article.toEntity(extensionId)
    }
}