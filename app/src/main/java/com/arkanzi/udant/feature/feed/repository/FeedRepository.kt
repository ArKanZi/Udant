package com.arkanzi.udant.feature.feed.repository

import android.util.Log
import com.arkanzi.udant.core.database.dao.ArticleDao
import com.arkanzi.udant.core.database.dao.FeedCategoryDao
import com.arkanzi.udant.core.mapper.toModel
import com.arkanzi.udant.core.mapper.toArticleEntities
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.core.model.FeedCategory
import com.arkanzi.udant.extension.thetimesofindia.TheTimesOfIndiaMain
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

    private val feedCategoryDao: FeedCategoryDao,

    private val toiMain: TheTimesOfIndiaMain,
) {
    private val repositoryScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun getArticles(): Flow<List<Article>> {

        val articles = articleDao
            .getAllArticles()
            .map { entities ->

                entities.map { entity ->

                    entity.toModel()
                }
            }

        repositoryScope.launch {
            articleDao
                .getArticlesNeedingEnrichment()
                .forEach { articleEntity ->
                    enrichIfNeeded(articleEntity.toModel())
                }
        }

        return articles
    }

    suspend fun fetchCategory(category: String) {
            val articles = toiMain.fetchCategory(category)
            articleDao.insertArticles(articles.toArticleEntities())
            articleDao
                .getArticlesNeedingEnrichment()
                .forEach { articleEntity ->
                    enrichIfNeeded(articleEntity.toModel())
                }
    }


    fun getCategories(): Flow<List<FeedCategory>> {
        return feedCategoryDao
            .getCategories()
            .map { entities ->
                entities
                    .groupBy { it.name }
                    .map { (_, categories) ->
                        categories.first().toModel()
                    }
            }
    }

    suspend fun fetchFirstFeedAndReplaceCache(): Result<Unit> {

        return try {

            val articles = toiMain.fetch()

            if (articles.isNotEmpty()) {
                articleDao.replaceArticles(articles.toArticleEntities())

                repositoryScope.launch {
                    articles
                        .forEach { article ->
                            enrichIfNeeded(article)
                        }
                }

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

            val articles = toiMain.fetch()

            articleDao.insertArticles(articles.toArticleEntities())

            repositoryScope.launch {
                articles
                    .forEach { article ->
                        enrichIfNeeded(article)
                    }
            }

            Result.success(Unit)

        } catch (exception: Exception) {

            Log.e(
                "FETCH_FEED_ERROR",
                exception.stackTraceToString()
            )

            Result.failure(exception)
        }
    }

    private suspend fun enrichIfNeeded(article: Article) {
        val needsImage = article.imageUrl.isNullOrBlank()
        val needsSummary = article.summary.isBlank()
        val needsAuthor = article.author.isNullOrBlank()
        if (needsImage || needsSummary || needsAuthor) {
            try {
                val enrichedArticle = toiMain.enrich(article)

                articleDao.updateEnrichment(
                    articleUrl = enrichedArticle.articleUrl,
                    imageUrl = enrichedArticle.imageUrl,
                    summary = enrichedArticle.summary,
                    author = enrichedArticle.author
                )
            } catch (e: Exception) {
                Log.e(
                    "ENRICH_ERROR",
                    e.stackTraceToString()
                )
            }
        }
    }
}

