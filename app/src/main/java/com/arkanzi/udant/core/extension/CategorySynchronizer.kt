package com.arkanzi.udant.core.extension

import com.arkanzi.udant.core.database.entity.FeedCategoryEntity
import com.arkanzi.udant.core.mapper.toFeedCategoryEntity
import com.arkanzi.udant.core.model.UdantExtension
import javax.inject.Inject

class CategorySynchronizer @Inject constructor() {

    fun synchronize(extension: UdantExtension, extensionId: String): List<FeedCategoryEntity> {
        val categories = extension.getCategories()
        val categoriesEntity = mutableListOf<FeedCategoryEntity>()

        categories.forEach { category ->
            categoriesEntity.add(
                category.toFeedCategoryEntity(extensionId =extensionId )
            )
        }
        return  categoriesEntity
    }
}
