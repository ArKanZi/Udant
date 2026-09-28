package com.arkanzi.udant.core.mapper

import com.arkanzi.udant.core.database.entity.CollectionEntity
import com.arkanzi.udant.core.model.Collection


fun CollectionEntity.toModel(): Collection {

    return Collection(
        id = id,
        name = name,
        color = color,
        isPinned = isPinned,
        sortOrder = sortOrder,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Collection.toEntity(): CollectionEntity {

    return CollectionEntity(
        id = id,
        name = name,
        color = color,
        isPinned = isPinned,
        sortOrder = sortOrder,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}