//package com.arkanzi.udant.extension.theindianexpress
//
//import android.util.Log
//import com.arkanzi.udant.core.model.Article
//import com.arkanzi.udant.core.model.FeedCategory
//import com.arkanzi.udant.core.model.UdantExtension
//import com.arkanzi.udant.core.util.toGenerateId
//import com.arkanzi.udant.extension.thetimesofindia.RssFeedDataSource
//import com.arkanzi.udant.extension.thetimesofindia.enricher.ArticleEnricher
//import com.arkanzi.udant.extension.thetimesofindia.toiSourceDetails
//import javax.inject.Inject
//
//class TheIndianExpress @Inject constructor(
//    private val rssFeedDataSource: RssFeedDataSource,
//    private val articleEnricher: ArticleEnricher
//) : UdantExtension {
//
//    override val id = "arkanzi.theindianexpress".toGenerateId()
//
//    override val name = "The Indian Express"
//
//    override val version = "1.0.0"
//
//    private val feedSources = toiSourceDetails
//
//    private var currentFeedIndex = 0
//
//    override fun getCategories(): List<FeedCategory> {
//        return feedSources.map {
//            FeedCategory(
//                name = it.category,
//                extensionId = id,
//                id = (id + it.category).toGenerateId()
//            )
//        }
//    }
//
//    override suspend fun fetch(): List<Article> {
//        val source = feedSources[currentFeedIndex]
//
//        val articles = rssFeedDataSource
//            .fetchArticles(source.sourceUrl)
//            .map { article ->
//                article.copy(
//                    sourceId = id,
//                    sourceName = source.sourceName,
//                    category = source.category
//                )
//            }
//
//        currentFeedIndex++
//
//        return articles
//    }
//
//    override suspend fun fetchCategory(
//        category: String
//    ): List<Article> {
//        val source = feedSources.first {
//            it.category == category
//        }
//
//        return rssFeedDataSource
//            .fetchArticles(source.sourceUrl)
//            .map { article ->
//                article.copy(
//                    sourceName = source.sourceName,
//                    category = source.category
//                )
//            }
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