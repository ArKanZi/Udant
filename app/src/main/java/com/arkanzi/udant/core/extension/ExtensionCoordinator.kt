package com.arkanzi.udant.core.extension

import android.util.Log
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.core.model.FeedResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExtensionCoordinator @Inject constructor(
    private val extensionManager: ExtensionManager
) {

    private val extensionNextPages = mutableMapOf<String, Int>()

    suspend fun fetch(extensionId: String): FeedResult {
        val extension = extensionManager.getExtension(extensionId)
            ?: return FeedResult(emptyList(), false)

        val nextPage = extensionNextPages.getOrPut(extensionId+"default") { 1 }

        if (nextPage == -1) {
            return FeedResult(emptyList(), false)
        }

        val result = extension.fetchHome(nextPage)

        updatePaginationState(
            extensionIdAndType = extensionId+"default",
            currentPage = nextPage,
            hasNextPage = result.hasNextPage
        )

        return result
    }

    suspend fun fetchCategory(
        extensionId: String,
        categoryName: String
    ): FeedResult {

        val extension = extensionManager.getExtension(extensionId)
            ?: return FeedResult(emptyList(), false)

        val nextPage = extensionNextPages.getOrPut(extensionId+categoryName) { 1 }

        if (nextPage == -1) {
            return FeedResult(emptyList(), false)
        }

        val result = extension.fetchCategory(categoryName,nextPage)

        updatePaginationState(
            extensionIdAndType = extensionId+categoryName,
            currentPage = nextPage,
            hasNextPage = result.hasNextPage
        )

        return result
    }

    suspend fun enrich(
        extensionId: String,
        article: Article
    ): Article? {
        val extension = extensionManager.getExtension(extensionId)
            ?: return null

        return try {
            extension.enrich(article)
        } catch (exception: Exception) {
            Log.e(
                "EXTENSION_ENRICH_ERROR",
                "Failed to enrich article with $extensionId",
                exception
            )
            null
        }
    }

    private fun updatePaginationState(
        extensionIdAndType: String,
        currentPage: Int,
        hasNextPage: Boolean
    ) {
        extensionNextPages[extensionIdAndType] =
            if (hasNextPage) currentPage + 1 else -1

        Log.d("updatePaginationState",extensionNextPages.toString())
    }
}