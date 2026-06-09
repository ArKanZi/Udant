package com.arkanzi.udant.core.network

import android.util.Log
import com.arkanzi.udant.core.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import javax.inject.Inject

class ArticleEnricher @Inject constructor(
    private val client: OkHttpClient
) {

    suspend fun enrich(
        article: Article
    ): Article = withContext(Dispatchers.IO) {

        try {

            if (
                article.imageUrl != null &&
                article.summary.isNotBlank()
            ) {
                return@withContext article
            }

            val request = Request.Builder()
                .url(article.articleUrl)
                .header(
                    "User-Agent",
                    "Mozilla/5.0"
                )
                .build()

            client.newCall(request)
                .execute()
                .use { response ->

                    if (!response.isSuccessful) {
                        return@withContext article
                    }

                    val html = response.body.string()

                    val document = Jsoup.parse(html)


                    // OpenGraph image first
                    var imageUrl = document
                        .select("meta[property=og:image]")
                        .attr("content")
                        .takeIf { it.isNotBlank() }

                    // Your custom site fallback
                    if (imageUrl == null) {
                        imageUrl = document
                            .selectFirst(
                                "div.bBzri div.WGttI img"
                            )
                            ?.attr("src")
                            ?.replace(
                                "width-400,height-225",
                                "width-1280,height-720"
                            )
                    }

                    // OpenGraph summary
                    val summary = document
                        .select("meta[property=og:description]")
                        .attr("content")
                        .takeIf { it.isNotBlank() }
                    return@withContext article.copy(
                        imageUrl = article.imageUrl ?: imageUrl,
                        summary = article.summary.ifBlank {
                            summary ?: article.summary
                        }
                    )
                }

        } catch (e: Exception) {

            Log.e(
                "ENRICH_ERROR",
                e.stackTraceToString()
            )

            article
        }
    }
}