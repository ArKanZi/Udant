package com.arkanzi.udant.core.mapper

import com.arkanzi.udant.core.database.entity.ExtensionEntity
import com.arkanzi.udant.core.model.Extension

fun ExtensionEntity.toModel(): Extension {
    return Extension(
        id = id,
        name = name,
    )
}

fun Extension.toEntity(): ExtensionEntity {
    return ExtensionEntity(
        id = id,
        name = name,
    )
}
