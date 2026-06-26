package com.arkanzi.udant.core.network

import android.util.Log
import com.arkanzi.udant.core.model.Article
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import javax.inject.Inject

class RssFeedDataSource @Inject constructor(
    private val client: OkHttpClient
) {

    suspend fun fetchArticles(
        feedUrl: String
    ): List<Article> = withContext(Dispatchers.IO) {

        try {

            val request = Request.Builder()
                .url(feedUrl)
                .header(
                    "User-Agent",
                    "Mozilla/5.0"
                )
                .build()

            client.newCall(request)
                .execute()
                .use { response ->

                    if (!response.isSuccessful) {
                        throw IllegalStateException(
                            "RSS request failed: ${response.code}"
                        )
                    }


                    val responseBody = response.body.string()

                    val feed = SyndFeedInput().build(
                        XmlReader(
                            responseBody.byteInputStream()
                        )
                    )

                    return@withContext feed.entries.map { entry ->

                        Article(
                            title = entry.title ?: "",

                            summary = Jsoup
                                .parse(
                                    entry.description?.value ?: ""
                                )
                                .text()
                                .trim(),

                            author = entry.author,

                            imageUrl = entry.enclosures
                                .firstOrNull()
                                ?.url,

                            sourceName = feed.title
                                ?: "Unknown Source",

                            articleUrl = entry.link ?: "",

                            publishedAt =
                                entry.publishedDate?.time
                                    ?: System.currentTimeMillis()

                        )
                    }
                }

        } catch (exception: Exception) {

            Log.e(
                "RSS_ERROR",
                exception.stackTraceToString()
            )

            throw exception
        }
    }
}