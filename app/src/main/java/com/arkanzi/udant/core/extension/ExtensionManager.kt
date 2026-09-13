package com.arkanzi.udant.core.extension

import com.arkanzi.udant.core.database.dao.FeedCategoryDao
import com.arkanzi.udant.core.database.entity.FeedCategoryEntity
import com.arkanzi.udant.core.mapper.toEntity
import com.arkanzi.udant.extension.thetimesofindia.TheTimesOfIndiaMain
import javax.inject.Inject

class ExtensionManager @Inject constructor(
    private val theTimesOfIndiaMain: TheTimesOfIndiaMain,
    private val feedCategoryDao: FeedCategoryDao
) {

    suspend fun syncCategories() {
        val categories = mutableListOf<FeedCategoryEntity>()

        // Udant built-in category
        categories.add(
            FeedCategoryEntity(
                id = "my_feed",
                name = "My Feed",
                extensionId = "udant"
            )
        )

        // Extension categories
        categories.addAll(
            theTimesOfIndiaMain
                .getCategories()
                .map { it.toEntity() }
        )

        feedCategoryDao.upsertCategories(categories)
    }
}