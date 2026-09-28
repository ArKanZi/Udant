package com.arkanzi.udant.feature.library.repository

import com.arkanzi.udant.core.database.dao.ArticleCollectionDao
import com.arkanzi.udant.core.database.dao.CollectionDao
import com.arkanzi.udant.core.database.entity.CollectionEntity
import com.arkanzi.udant.core.database.entity.SavedArticleEntity
import com.arkanzi.udant.core.mapper.toEntity
import com.arkanzi.udant.core.mapper.toModel
import com.arkanzi.udant.core.model.Collection
import com.arkanzi.udant.feature.library.model.LibraryCollection
import com.arkanzi.udant.feature.library.ui.components.collection.generateCollectionColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class LibraryRepository @Inject constructor(
    private val collectionDao: CollectionDao,
    private val articleCollectionDao: ArticleCollectionDao
) {

    fun getCollections(): Flow<List<LibraryCollection>> {
        return combine(
            collectionDao.getCollectionsWithArticleCount(),
            articleCollectionDao.getDefaultArticleCount()
        ) { collections, defaultCount ->

            buildList {

                if (defaultCount > 0) {
                    add(
                        LibraryCollection.Default(
                            articleCount = defaultCount
                        )
                    )
                }

                addAll(
                    collections.map { collection ->
                        LibraryCollection.User(
                            collection = CollectionEntity(
                                id = collection.id,
                                name = collection.name,
                                color = collection.color,
                                isPinned = collection.isPinned,
                                sortOrder = collection.sortOrder,
                                createdAt = collection.createdAt,
                                updatedAt = collection.updatedAt
                            ).toModel(),
                            articleCount = collection.articleCount
                        )
                    }
                )
            }
        }
    }

    suspend fun createCollection(name: String): Boolean {
        if (collectionDao.existsByName(name)) {
            return false
        }

        val now = System.currentTimeMillis()

        collectionDao.insertCollection(
            CollectionEntity(
                name = name,
                color = generateCollectionColor().value.toLong(),
                createdAt = now,
                updatedAt = now
            )
        )

        return true
    }

    suspend fun editCollection(name: String,collection: Collection): Boolean {
        if (collectionDao.existsByName(name)) {
            return false
        }

        val now = System.currentTimeMillis()

        return collectionDao.updateCollection(collection
            .toEntity().copy(name = name,updatedAt = now))==1

    }

    suspend fun collectionExists(name: String): Boolean{
        return collectionDao.existsByName(name)
    }


    fun getArticles(
        collection: LibraryCollection
    ): Flow<List<SavedArticleEntity>> {
        return when (collection) {
            is LibraryCollection.Default ->
                articleCollectionDao.getDefaultArticles()

            is LibraryCollection.User ->
                articleCollectionDao.getArticlesInCollection(
                    collection.collection.id
                )
        }
    }
}