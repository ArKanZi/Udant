package com.arkanzi.udant.core.extension


import com.arkanzi.udant.core.database.dao.FeedCategoryDao
import com.arkanzi.udant.core.model.UdantExtension
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExtensionManager @Inject constructor(
    private val extensions: Set<@JvmSuppressWildcards UdantExtension>,
    private val extensionRegistrar: ExtensionRegistrar,
    private val categorySynchronizer: CategorySynchronizer,
    private val feedCategoryDao: FeedCategoryDao,

    ) {

    private val loadedExtensions = mutableMapOf<String, UdantExtension>()

    suspend fun start() {

        extensions.forEach { extension ->
            val extensionId = extensionRegistrar.upsertExtensions(extension)
            loadedExtensions[extensionId] = extension
            syncCategories(extension, extensionId)
        }
    }

    fun getExtension(extensionId: String): UdantExtension? {
        return loadedExtensions[extensionId]
    }



    suspend fun syncCategories(extension: UdantExtension, extensionId: String) {
        val categories = categorySynchronizer.synchronize(extension,extensionId)
        feedCategoryDao.upsertCategories(categories)
    }
}