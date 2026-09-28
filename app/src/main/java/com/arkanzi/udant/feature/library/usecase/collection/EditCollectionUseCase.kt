package com.arkanzi.udant.feature.library.usecase.collection

import com.arkanzi.udant.core.model.Collection
import com.arkanzi.udant.feature.library.repository.LibraryRepository
import javax.inject.Inject


class EditCollectionUseCase @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(name: String,collection: Collection): Boolean {
        val normalizedName = name.trim()

        if (normalizedName.isBlank()) {
            return false
        }

        if (repository.collectionExists(normalizedName)) {
            return false
        }

        repository.editCollection(normalizedName,collection)

        return true
    }
}