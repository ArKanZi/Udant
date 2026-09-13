package com.arkanzi.udant.core.mapper

import com.arkanzi.udant.core.database.entity.FeedCategoryEntity
import com.arkanzi.udant.core.model.FeedCategory

fun FeedCategoryEntity.toModel(): FeedCategory {
    return FeedCategory(
        id = id,
        name = name,
        extensionId = extensionId
    )
}

fun FeedCategory.toEntity(): FeedCategoryEntity {
    return FeedCategoryEntity(
        id = id,
        name = name,
        extensionId = extensionId
    )
}
