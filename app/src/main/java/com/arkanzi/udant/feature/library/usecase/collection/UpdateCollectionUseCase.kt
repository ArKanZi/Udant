package com.arkanzi.udant.feature.library.usecase.collection

import com.arkanzi.udant.core.model.CollectionModel
import com.arkanzi.udant.feature.library.repository.LibraryRepository
import javax.inject.Inject


class UpdateCollectionUseCase @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend fun updateName(
        name: String,
        collectionModel: CollectionModel
    ): Boolean {
        val normalizedName = name.trim()

        if (normalizedName.isBlank()) {
            return false
        }

        if (repository.collectionExists(normalizedName)) {
            return false
        }

        return repository.updateCollection(
            collectionModel.copy(
                name = normalizedName,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun updatePinned(
        isPinned: Boolean,
        collectionModel: CollectionModel
    ): Boolean {
        return repository.updateCollection(
            collectionModel.copy(
                isPinned = isPinned,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}