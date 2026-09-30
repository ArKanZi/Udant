package com.arkanzi.udant.feature.library.usecase.collection

import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toColorLong
import com.arkanzi.udant.core.model.CollectionModel
import com.arkanzi.udant.feature.library.repository.LibraryRepository
import com.arkanzi.udant.feature.library.ui.components.collection.generateCollectionColor
import java.util.UUID
import javax.inject.Inject

class CreateCollectionUseCase @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(name: String): Boolean {
        val normalizedName = name.trim()

        if (normalizedName.isBlank()) {
            return false
        }

        if (repository.collectionExists(normalizedName)) {
            return false
        }

        val now = System.currentTimeMillis()

        val collectionModel = CollectionModel(
            id = UUID.randomUUID().toString(),
            name = name,
            color = generateCollectionColor().toArgb().toLong(),
            isPinned = false,
            createdAt = now,
            updatedAt = now
        )

        return repository.insertCollection(collectionModel)


    }
}