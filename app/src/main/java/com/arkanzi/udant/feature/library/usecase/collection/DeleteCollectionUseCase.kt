package com.arkanzi.udant.feature.library.usecase.collection

import com.arkanzi.udant.core.model.CollectionModel
import com.arkanzi.udant.feature.library.repository.LibraryRepository
import javax.inject.Inject

class DeleteCollectionUseCase @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(collectionModel: CollectionModel): Boolean {
        return repository.deleteCollection(collectionModel)
    }
}