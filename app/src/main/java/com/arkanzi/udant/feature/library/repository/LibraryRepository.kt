package com.arkanzi.udant.feature.library.repository

import com.arkanzi.udant.core.database.dao.CollectionDao
import com.arkanzi.udant.core.database.entity.CollectionEntity
import com.arkanzi.udant.core.mapper.toEntity
import com.arkanzi.udant.core.model.CollectionModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LibraryRepository @Inject constructor(
    private val collectionDao: CollectionDao,
) {

    fun getAllCollections(): Flow<List<CollectionEntity>> {
        return collectionDao.getAllCollections()
        }

    fun getDefaultArticleCount(): Flow<Long>{
        return collectionDao.getDefaultArticleCount()
    }

    suspend fun insertCollection(collectionModel: CollectionModel): Boolean =
        collectionDao.insertCollection(collectionModel.toEntity()) != -1L

    suspend fun updateCollection(collectionModel: CollectionModel): Boolean {
        return collectionDao.updateCollection(collectionModel.toEntity())==1
    }

    suspend fun deleteCollection(collectionModel: CollectionModel): Boolean {
        return collectionDao.deleteCollection(collectionModel.toEntity())==1
    }

    suspend fun collectionExists(name: String): Boolean{
        return collectionDao.existsByName(name)
    }


}