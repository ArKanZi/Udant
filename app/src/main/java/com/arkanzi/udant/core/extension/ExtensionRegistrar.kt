package com.arkanzi.udant.core.extension

import com.arkanzi.udant.core.database.dao.ExtensionDao
import com.arkanzi.udant.core.database.entity.ExtensionEntity
import com.arkanzi.udant.core.model.UdantExtension
import com.arkanzi.udant.core.util.normalize
import com.arkanzi.udant.core.util.toGenerateId
import javax.inject.Inject

class ExtensionRegistrar @Inject constructor(
    private val extensionDao: ExtensionDao
) {
    suspend fun upsertExtensions(extension: UdantExtension): String {
        val id = normalize(extension.developerName + "." + extension.name).toGenerateId()
        extensionDao.upsertExtension(
            ExtensionEntity(
                id = id,
                name = extension.name,
                version = extension.version
            )
        )
        return id
    }
}