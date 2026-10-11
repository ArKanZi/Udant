package com.arkanzi.udant.extension.thetimesofindia

import android.util.Log
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.extension.thetimesofindia.model.ToiHomeItemDto
import com.arkanzi.udant.extension.thetimesofindia.model.ToiHomeResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class ToiHomeDataSource @Inject constructor(
    private val client: OkHttpClient
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    suspend fun fetchArticles(path: String = HOME_PATH): List<Article> =
        withContext(Dispatchers.IO) {
            val url = (BASE_URL + LISTING_PATH).toHttpUrl().newBuilder()
                .addQueryParameter("path", path)
                .addQueryParameter("pc", "toi")
                .addQueryParameter("fv", FEED_VERSION)
                .addQueryParameter("pfm", "aos")
                .addQueryParameter("cloud", "n")
                .addQueryParameter("client", "toi")
                .build()

            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("User-Agent", USER_AGENT)
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IllegalStateException("TOI feed request failed: HTTP ${response.code}")
                    }

                    val body = response.body.string().orEmpty()
                    if (body.isBlank()) {
                        throw IllegalStateException("TOI feed response was empty")
                    }

                    val dto = try {
                        json.decodeFromString<ToiHomeResponseDto>(body)
                    } catch (exception: SerializationException) {
                        throw IllegalStateException("Could not parse TOI feed response", exception)
                    }

                    // Keep every item type in the DTO; only map entries with enough data to form an Article.
                    dto.items.mapNotNull { item -> item.toArticleOrNull() }
                }
            } catch (exception: Exception) {
                Log.e(TAG, "Failed to fetch TOI feed path=$path", exception)
                throw exception
            }
        }

    private fun ToiHomeItemDto.toArticleOrNull(): Article? {
        val url = wu.cleanOrNull() ?: su.cleanOrNull() ?: return null
        val title = hl.cleanOrNull()
            ?: headline?.text.cleanOrNull()
            ?: title.cleanOrNull()
            ?: return null

        val rawImageId = primeImage?.firstOrNull()?.id.cleanOrNull()
            ?: imageid.cleanOrNull()
        val imageUrl = rawImageId?.let(::buildImageUrl)

        val publishedAt = upd.toEpochMillisOrNull()
            ?: lpt.toEpochMillisOrNull()
            ?: updatedTime.toEpochMillisOrNull()
            ?: System.currentTimeMillis()

        return Article(
            articleId = id.cleanOrNull() ?: url,
            title = title,
            summary = aiSummaryDesc.cleanOrNull()
                ?: caption.cleanOrNull()
                ?: headline?.context.cleanOrNull()
                .orEmpty(),
            imageUrl = imageUrl,
            articleUrl = url,
            publishedAt = publishedAt,
            sourceId = "",
            sourceName = pubInfo?.pn.cleanOrNull()
                ?: pubInfo?.pnEng.cleanOrNull()
                ?: "The Times of India",
            author = au.cleanOrNull()
                ?: au1.cleanOrNull()
                ?: author?.firstNotNullOfOrNull { it.name.cleanOrNull() },
            category = cat.cleanOrNull() ?: "Top Stories"
        )
    }

    private fun buildImageUrl(rawId: String): String? {
        val id = rawId.substringBefore('&').trim()
        if (id.isBlank()) return null
        return "https://static.toiimg.com/thumb/msid-$id,width-400,height-225,resizemode-4/$id.jpg"
    }

    private fun String?.cleanOrNull(): String? =
        this?.trim()?.takeIf { it.isNotEmpty() && !it.equals("null", ignoreCase = true) }

    private fun String?.toEpochMillisOrNull(): Long? =
        this.cleanOrNull()?.toLongOrNull()?.let { value ->
            when {
                value <= 0L -> null
                value < 100_000_000_000L -> value * 1000L // seconds -> milliseconds
                else -> value
            }
        }

    companion object {
        private const val TAG = "ToiHomeDataSource"
        private const val BASE_URL = "https://plus.timesofindia.com"
        private const val LISTING_PATH = "/aufs/feed/listing/get"
        private const val FEED_VERSION = "1512"
        private const val HOME_PATH = "toi/home/india"
        private const val USER_AGENT = "TOI-Android-Udant/1.0"
    }
}
