package com.arkanzi.udant.extension.thetimesofindia.enricher

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
                article.summary.isNotBlank() &&
                !article.author.isNullOrBlank()
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

                    val author = document
                        .selectFirst("div.inl71 div.byline")
                        ?.let { byline ->
                            byline.selectFirst("a")
                                ?.text()
                                ?.trim()
                                ?.takeIf { it.isNotBlank() }
                                ?: byline.text()
                                    .takeIf { "/" in it }
                                    ?.substringBefore("/")
                                    ?.trim()
                                    ?.takeIf { it.isNotBlank() }
                        }

                    return@withContext article.copy(
                        imageUrl = article.imageUrl ?: imageUrl,
                        summary = article.summary.ifBlank {
                            summary ?: article.summary
                        },
                        author = article.author
                            ?.takeIf { it.isNotBlank() }
                            ?: author
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