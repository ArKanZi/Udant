package com.arkanzi.udant.feature.feed.repository

import android.util.Log
import com.arkanzi.udant.core.database.dao.ArticleDao
import com.arkanzi.udant.core.database.dao.ExtensionDao
import com.arkanzi.udant.core.database.dao.FeedCategoryDao
import com.arkanzi.udant.core.extension.ExtensionCoordinator
import com.arkanzi.udant.core.mapper.toArticleEntities
import com.arkanzi.udant.core.mapper.toModel
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.core.model.FeedCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(
    private val articleDao: ArticleDao,
    private val feedCategoryDao: FeedCategoryDao,
    private val extensionDao: ExtensionDao,
    private val extensionCoordinator: ExtensionCoordinator
) {

    private val repositoryScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun getExtensionIds(): Flow<List<String>> {
        return extensionDao.getExtensions()
            .map { extensions ->
                extensions.map { it.id }
            }
    }
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

                    val article = articleEntity.toModel()

                    val enrichedArticle = extensionCoordinator.enrich(
                        extensionId = article.sourceId,
                        article = article
                    ) ?: return@forEach

                    enrichIfNeeded(article = enrichedArticle)
                }
        }

        return articles
    }

    suspend fun fetchCategory(
        extensionIds: List<String>,
        categoryName: String
    ) {
        extensionIds.forEach { extensionId ->

            val result = extensionCoordinator.fetchCategory(extensionId,categoryName)

            articleDao.insertArticles(
                result.articles.toArticleEntities(extensionId)
            )

            result.articles.forEach { article ->

                val enrichedArticle = extensionCoordinator.enrich(
                    extensionId = article.sourceId,
                    article = article
                ) ?: return@forEach

                enrichIfNeeded(
                    article = enrichedArticle
                )
            }
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

    suspend fun fetchFirstFeedAndReplaceCache(
        extensionIds: List<String>
    ): Result<Unit> = try {
        coroutineScope {
            extensionIds.map { extensionId ->

                async {
                    try {

                        val result = extensionCoordinator.fetch(extensionId)

                        if (result.articles.isNotEmpty()) {
                            articleDao.replaceArticles(
                                result.articles.toArticleEntities(extensionId)
                            )

                            repositoryScope.launch {
                                result.articles.forEach { article ->

                                    val enrichedArticle = extensionCoordinator.enrich(
                                        extensionId = article.sourceId,
                                        article = article
                                    ) ?: return@forEach

                                    enrichIfNeeded(
                                        article = enrichedArticle
                                    )
                                }
                            }
                        }
                    } catch (exception: Exception) {
                        Log.e(
                            "FETCH_FEED_ERROR1",
                            "Failed to fetch $extensionId",
                            exception
                        )
                    }
                }
            }.awaitAll()
        }

        Result.success(Unit)

    } catch (exception: Exception) {

        Log.e(
            "FETCH_FEED_ERROR",
            exception.stackTraceToString()
        )

        Result.failure(exception)
    }

    suspend fun fetchNextFeed(
        extensionIds: List<String>
    ): Result<Unit> = try {

        coroutineScope {
            extensionIds.map { extensionId ->

                async {
                    try {
                        val result = extensionCoordinator.fetch(extensionId)

                        if (result.articles.isNotEmpty()) {
                            articleDao.insertArticles(
                                result.articles.toArticleEntities(extensionId)
                            )

                            repositoryScope.launch {
                                result.articles.forEach { article ->

                                    val enrichedArticle = extensionCoordinator.enrich(
                                        extensionId = article.sourceId,
                                        article = article
                                    ) ?: return@forEach


                                    enrichIfNeeded(article = enrichedArticle)
                                }
                            }
                        }
                    } catch (exception: Exception) {
                        Log.e(
                            "FETCH_FEED_ERROR",
                            "Failed to fetch $extensionId",
                            exception
                        )
                    }
                }
            }.awaitAll()
        }

        Result.success(Unit)

    } catch (exception: Exception) {

        Log.e(
            "FETCH_FEED_ERROR",
            exception.stackTraceToString()
        )

        Result.failure(exception)
    }


    private suspend fun enrichIfNeeded(
        article: Article
    ) {
        val needsImage = article.imageUrl.isNullOrBlank()
        val needsSummary = article.summary.isBlank()
        val needsAuthor = article.author.isNullOrBlank()

        if (needsImage || needsSummary || needsAuthor) {
            try {

                articleDao.updateEnrichment(
                    articleUrl = article.articleUrl,
                    imageUrl = article.imageUrl,
                    summary = article.summary,
                    author = article.author
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