package com.arkanzi.udant.core.mapper

import com.arkanzi.udant.core.database.entity.CollectionEntity
import com.arkanzi.udant.core.model.CollectionModel


fun CollectionEntity.toModel(): CollectionModel {

    return CollectionModel(
        id = id,
        name = name,
        color = color,
        isPinned = isPinned,
        sortOrder = sortOrder,
        articleCount = articleCount,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun CollectionModel.toEntity(): CollectionEntity {

    return CollectionEntity(
        id = id,
        name = name,
        color = color,
        isPinned = isPinned,
        sortOrder = sortOrder,
        articleCount = articleCount,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}