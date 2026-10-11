//package com.arkanzi.udant.extension.thetimesofindia
//
//import android.util.Log
//import com.arkanzi.udant.core.model.Article
//import com.arkanzi.udant.core.model.ExtensionCategory
//import com.arkanzi.udant.core.model.FeedResult
//import com.arkanzi.udant.core.model.UdantExtension
//import com.arkanzi.udant.extension.thetimesofindia.enricher.ArticleEnricher
//import javax.inject.Inject
//
//class TheTimesOfIndiaMain @Inject constructor(
//    private val rssFeedDataSource: RssFeedDataSource,
//    private val articleEnricher: ArticleEnricher
//) : UdantExtension {
//
//    override val name = "The Times of India"
//
//    override val developerName = "Arkanzi"
//
//    override val version = "1.0.0"
//
//    override val baseUrl = "https://timesofindia.indiatimes.com"
//
//    private val feedSources = toiSourceDetails
//
//    private var currentFeedIndex = 0
//
//    override fun getCategories(): List<ExtensionCategory> {
//        return categories
//    }
//
//    override suspend fun fetchHome(page: Int): FeedResult {
//        val source = feedSources[currentFeedIndex]
//
//        val articles = rssFeedDataSource
//            .fetchArticles(source.sourceUrl)
//            .map { article ->
//                article.copy(
//                    sourceId = "",
//                    sourceName = source.sourceName,
//                    category = source.category
//                )
//            }
//        val hasNextPage = currentFeedIndex < feedSources.size - 1
//        currentFeedIndex++
//
//        return FeedResult(
//            articles = articles,
//            hasNextPage = hasNextPage
//        )
//    }
//
//    override suspend fun fetchCategory(
//        categoryName: String
//    ): FeedResult {
//        val category = CategoryList.first {
//            it.name == categoryName
//        }
//
//        val article = rssFeedDataSource
//            .fetchArticles(baseUrl+category.url)
//            .map { article ->
//                article.copy(
//                    sourceName = name,
//                    category = category.name
//                )
//            }
//        val hasNextPage = false
//        return FeedResult(
//            articles = article,
//            hasNextPage = hasNextPage
//        )
//    }
//
//    override suspend fun enrich(
//        article: Article
//    ): Article {
//        return try {
//            articleEnricher.enrich(article)
//        } catch (exception: Exception) {
//            Log.e(
//                "ENRICH_ERROR",
//                exception.stackTraceToString()
//            )
//
//            article
//        }
//    }
//}