package com.arkanzi.udant.feature.library.usecase.collection


import com.arkanzi.udant.core.model.Collection
import com.arkanzi.udant.feature.library.model.LibraryCollection
import com.arkanzi.udant.feature.library.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CollectionApplicationService @Inject constructor(
    private val createCollectionUseCase: CreateCollectionUseCase,
    private val editCollectionUseCase: EditCollectionUseCase,
    private val collectionExistsUseCase: CollectionExistsUseCase,
    private val repository: LibraryRepository
) {
    fun getCollections(): Flow<List<LibraryCollection>> {
        return repository.getCollections()
    }

    suspend fun createCollection(name: String) {
        createCollectionUseCase(name.trim())
    }

    suspend fun editCollection(name:String,collection: Collection){
        editCollectionUseCase(name.trim(),collection)
    }

    suspend fun collectionExists(name: String): Boolean {
        return collectionExistsUseCase(name.trim())
    }
}