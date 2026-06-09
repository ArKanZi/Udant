package com.arkanzi.udant.core.mapper

import com.arkanzi.udant.core.database.entity.ArticleEntity
import com.arkanzi.udant.core.model.Article

fun ArticleEntity.toArticle(): Article {

    return Article(
        articleId = articleId,
        title = title,
        summary = summary,
        author = author,
        imageUrl = imageUrl,
        sourceName = sourceName,
        articleUrl = articleUrl,
        publishedAt = publishedAt,
        category = category
    )
}

fun Article.toArticleEntity(): ArticleEntity {

    return ArticleEntity(
        articleId = articleId,
        title = title,
        summary = summary,
        author = author,
        imageUrl = imageUrl,
        sourceName = sourceName,
        articleUrl = articleUrl,
        publishedAt = publishedAt,
        category = category
    )
}

fun List<Article>.toArticleEntities(): List<ArticleEntity> {

    return map { article ->

        article.toArticleEntity()
    }
}