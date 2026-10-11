package com.arkanzi.udant.core.mapper

import com.arkanzi.udant.core.database.entity.FeedCategoryEntity
import com.arkanzi.udant.core.model.ExtensionCategory
import com.arkanzi.udant.core.model.FeedCategory
import com.arkanzi.udant.core.util.toGenerateId

fun FeedCategoryEntity.toModel(): FeedCategory {
    return FeedCategory(
        id = id,
        name = name,
        categoryType=categoryType,
        extensionId = extensionId
    )
}

fun FeedCategory.toEntity(extensionId: String): FeedCategoryEntity {
    return FeedCategoryEntity(
        id = (extensionId + name).toGenerateId(),
        name = name,
        categoryType=categoryType,
        extensionId = extensionId
    )
}

fun ExtensionCategory.toFeedCategoryEntity(extensionId: String): FeedCategoryEntity {
    return FeedCategoryEntity(
        id = (extensionId + name).toGenerateId(),
        name = name,
        categoryType=type,
        extensionId = extensionId
    )
}
