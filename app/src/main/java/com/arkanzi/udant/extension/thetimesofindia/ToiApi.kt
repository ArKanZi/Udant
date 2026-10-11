package com.arkanzi.udant.extension.thetimesofindia

import java.io.IOException
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.core.model.FeedResult
import com.arkanzi.udant.extension.thetimesofindia.model.ToiFeedResponseDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class ToiApi @Inject constructor(
    private val client: OkHttpClient,
    @param:ApplicationContext private val appContext: Context
) {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    suspend fun fetchHomeArticles(url: String): FeedResult =
        withContext(Dispatchers.IO) {
            try {

                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException(
                            "TOI request failed: HTTP ${response.code}"
                        )
                    }

                    val body = response.body.string()

                    val feed = json.decodeFromString<ToiFeedResponseDto>(body)

                    val articles = feed.items
                        .asSequence()
                        .mapNotNull { item ->
                            val title = item.hl
                                ?.takeIf { it.isNotBlank() }
                                ?: item.headline?.let {
                                    "${it.context.orEmpty()}${it.text.orEmpty()}".trim()
                                }
                                ?: return@mapNotNull null

                            val articleUrl = item.wu
                                ?.takeIf { it.isNotBlank() }
                                ?: return@mapNotNull null

                            val imageId = item.imageid
                                ?.substringBefore("&")
                                ?.takeIf { it.isNotBlank() }

                            Article(
                                articleId = "",
                                title = title,
                                summary = item.aiSummaryDesc.orEmpty(),
                                imageUrl = imageId?.let {
                                    "https://static.toiimg.com/photo.cms?photoid=$it"
                                },
                                articleUrl = articleUrl,
                                publishedAt = item.dl?.toLongOrNull()
                                    ?: item.upd?.toLongOrNull()
                                    ?: 0L,
                                sourceId = "",
                                sourceName = "The Times of India",
                                author = item.au?.takeIf { it.isNotBlank() },
                                category = item.cat ?: "India"
                            )
                        }
                        .toList()

                    FeedResult(
                        articles = articles,
                        hasNextPage = feed.pg?.hasNextPage ?: false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("ToiApi", "Failed to fetch TOI articles: $url", e)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        appContext,
                        "Failed to fetch articles",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                FeedResult(
                    articles = emptyList(),
                    hasNextPage = false
                )
            }
        }
}