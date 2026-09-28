package com.arkanzi.udant.feature.library.usecase.collection

import com.arkanzi.udant.feature.library.repository.LibraryRepository
import javax.inject.Inject

class CollectionExistsUseCase @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(name: String): Boolean {
        return repository.collectionExists(name)
    }
}