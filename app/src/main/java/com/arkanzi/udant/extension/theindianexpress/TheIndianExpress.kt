package com.arkanzi.udant.extension.theindianexpress

import android.util.Log
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.core.model.FeedCategory
import com.arkanzi.udant.extension.thetimesofindia.RssFeedDataSource
import com.arkanzi.udant.extension.thetimesofindia.enricher.ArticleEnricher
import com.arkanzi.udant.extension.thetimesofindia.toiSourceDetails
import javax.inject.Inject

class TheIndianExpress @Inject constructor(
    private val rssFeedDataSource: RssFeedDataSource,
    private val articleEnricher: ArticleEnricher
) {
    private val feedSources = toiSourceDetails
    private var currentFeedIndex = 0

    fun getCategories(): List<FeedCategory> {
        return feedSources.map {
            FeedCategory(
                name = it.category,
            )
        }
    }

    suspend fun fetch(): List<Article> {

        val source = feedSources[currentFeedIndex]

        val articles = rssFeedDataSource
            .fetchArticles(source.sourceUrl)
            .map { article ->

                article.copy(
                    sourceName = source.sourceName,
                    category = source.category
                )
            }

        currentFeedIndex++


        return articles
    }

    suspend fun fetchCategory(category: String): List<Article> {
        val source= feedSources.first { it.category == category }
        val article = rssFeedDataSource
            .fetchArticles(source.sourceUrl)
            .map { article ->
                article.copy(
                    sourceName = source.sourceName,
                    category = source.category
                )
            }
        return article
    }

    suspend fun enrich(
        article: Article
    ): Article {
        try {
            return articleEnricher.enrich(article)
        } catch (exception: Exception) {
            Log.e(
                "ENRICH_ERROR",
                exception.stackTraceToString()
            )
        }
        return article
    }
}
