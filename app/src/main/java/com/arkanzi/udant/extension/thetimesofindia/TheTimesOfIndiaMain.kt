package com.arkanzi.udant.extension.thetimesofindia

import android.util.Log
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.core.model.FeedCategory
import com.arkanzi.udant.extension.thetimesofindia.enricher.ArticleEnricher
import javax.inject.Inject

class TheTimesOfIndiaMain @Inject constructor(
    private val rssFeedDataSource: RssFeedDataSource,
    private val articleEnricher: ArticleEnricher
) {
    private val feedSources = toiSourceDetails
    private var currentFeedIndex = 0

    fun getCategories(): List<FeedCategory> {
        return feedSources.map {
            FeedCategory(
                id = it.category,
                name = it.category,
                extensionId = TheTimesOfIndiaMain::class.java.simpleName
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
