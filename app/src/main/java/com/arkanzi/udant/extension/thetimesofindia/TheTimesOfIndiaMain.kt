package com.arkanzi.udant.extension.thetimesofindia

import android.util.Log
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.core.model.ExtensionCategory
import com.arkanzi.udant.core.model.FeedResult
import com.arkanzi.udant.core.model.UdantExtension
import com.arkanzi.udant.extension.thetimesofindia.enricher.ArticleEnricher
import javax.inject.Inject

class TheTimesOfIndiaMain @Inject constructor(
    private val rssFeedDataSource: RssFeedDataSource,
    private val articleEnricher: ArticleEnricher,
    private val toiApi: ToiApi
) : UdantExtension {

    override val name = "The Times of India"

    override val developerName = "Arkanzi"

    override val version = "1.0.0"
    override val baseUrl = "https://timesofindia.indiatimes.com"


    override fun getCategories(): List<ExtensionCategory> {
        return categories
    }

    override suspend fun fetchHome(page: Int): FeedResult {
        return toiApi.fetchHomeArticles(ToiConfig.BASE_URL+myFeed.url)
    }

    override suspend fun fetchCategory(
        categoryName: String,
        page: Int
    ): FeedResult {

        val category = categories.first {
            it.name == categoryName
        }
    Log.d("category",category.toString())
        return toiApi.fetchHomeArticles(feedCategoryUrl(category.url, page = page))
    }

    override suspend fun enrich(
        article: Article
    ): Article {
        return try {
            articleEnricher.enrich(article)
        } catch (exception: Exception) {
            Log.e(
                "ENRICH_ERROR",
                exception.stackTraceToString()
            )

            article
        }
    }
}