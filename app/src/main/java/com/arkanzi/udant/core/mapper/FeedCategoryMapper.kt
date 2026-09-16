package com.arkanzi.udant.core.mapper

import com.arkanzi.udant.core.database.entity.FeedCategoryEntity
import com.arkanzi.udant.core.model.FeedCategory
import com.arkanzi.udant.core.util.toGenerateId
import java.util.UUID

fun FeedCategoryEntity.toModel(): FeedCategory {
    return FeedCategory(
        name = name,
    )
}

fun FeedCategory.toEntity(extensionId: String): FeedCategoryEntity {
    return FeedCategoryEntity(
        id = (extensionId + name).toGenerateId(),
        name = name,
        extensionId = extensionId
    )
}
