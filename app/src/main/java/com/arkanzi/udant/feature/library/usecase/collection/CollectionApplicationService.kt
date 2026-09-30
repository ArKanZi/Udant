package com.arkanzi.udant.feature.library.usecase.collection


import com.arkanzi.udant.core.mapper.toModel
import com.arkanzi.udant.core.model.CollectionModel
import com.arkanzi.udant.feature.library.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CollectionApplicationService @Inject constructor(
    private val createCollectionUseCase: CreateCollectionUseCase,
    private val updateCollectionUseCase: UpdateCollectionUseCase,
    private val deleteCollectionUseCase: DeleteCollectionUseCase,
    private val collectionExistsUseCase: CollectionExistsUseCase,
    private val repository: LibraryRepository
) {
    fun getCollections(): Flow<List<CollectionModel>> {
        return repository.getAllCollections().map {entities ->
            entities.map { entity ->
                entity.toModel()
            }
        }
    }

    fun getDefaultArticleCount(): Flow<Long>{
        return repository.getDefaultArticleCount()
    }

    suspend fun createCollection(name: String) {
        createCollectionUseCase(name.trim())
    }

    suspend fun updateCollectionName(name:String, collectionModel: CollectionModel){
        updateCollectionUseCase.updateName(name.trim(),collectionModel)
    }

    suspend fun updateCollectionPinned(isPinned: Boolean, collectionModel: CollectionModel){
        updateCollectionUseCase.updatePinned(isPinned,collectionModel)
    }

    suspend fun deleteCollection(collectionModel: CollectionModel): Boolean{
        return deleteCollectionUseCase(collectionModel)
    }

    suspend fun collectionExists(name: String): Boolean {
        return collectionExistsUseCase(name.trim())
    }
}