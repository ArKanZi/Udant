package com.arkanzi.udant.feature.feed.repository

import android.util.Log
import com.arkanzi.udant.core.database.dao.ArticleDao
import com.arkanzi.udant.core.mapper.toArticle
import com.arkanzi.udant.core.mapper.toArticleEntities
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.core.network.ArticleEnricher
import com.arkanzi.udant.core.network.RssFeedService
import com.arkanzi.udant.feature.feed.data.source.toiFeedSources
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(

    private val articleDao: ArticleDao,

    private val rssFeedService: RssFeedService,

    private val articleEnricher: ArticleEnricher

) {
    private val feedSources = toiFeedSources
    private var currentFeedIndex = 0
    private val repositoryScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    fun getArticles(): Flow<List<Article>> {

        return articleDao
            .getAllArticles()
            .map { entities ->

                entities.map { entity ->

                    entity.toArticle()
                }
            }
    }

    suspend fun fetchFirstFeedAndReplaceCache(): Result<Unit> {

        return try {

            val articles = fetchFeed()

            if (articles.isNotEmpty()) {
                articleDao.replaceArticles(articles.toArticleEntities())
                enrichArticles(articles)
                Result.success(Unit)
            } else {
                Result.failure(IllegalStateException("Feed returned no articles"))
            }


        } catch (exception: Exception) {

            Log.e(
                "FETCH_FEED_ERROR",
                exception.stackTraceToString()
            )

            Result.failure(exception)
        }
    }

    suspend fun fetchNextFeed(): Result<Unit> {

        return try {

            val articles = fetchFeed()

            articleDao.insertArticles(articles.toArticleEntities())

            enrichArticles(articles)

            Result.success(Unit)

        } catch (exception: Exception) {

            Log.e(
                "FETCH_FEED_ERROR",
                exception.stackTraceToString()
            )

            Result.failure(exception)
        }
    }

    private suspend fun fetchFeed(): List<Article> {

        val source = feedSources[currentFeedIndex]

        val articles = rssFeedService
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

    private fun enrichArticles(
        articles: List<Article>
    ) {

        articles.forEach { article ->

            val needsImage =
                article.imageUrl.isNullOrBlank()

            val needsSummary =
                article.summary.isBlank()

            if (
                needsImage ||
                needsSummary
            ) {

                repositoryScope.launch {

                    try {

                        val enrichedArticle =
                            articleEnricher.enrich(article)

                        articleDao.updateEnrichment(
                            articleUrl = enrichedArticle.articleUrl,
                            imageUrl = enrichedArticle.imageUrl,
                            summary = enrichedArticle.summary
                        )

                    } catch (exception: Exception) {

                        Log.e(
                            "ENRICH_ERROR",
                            exception.stackTraceToString()
                        )
                    }
                }
            }
        }
    }
}

