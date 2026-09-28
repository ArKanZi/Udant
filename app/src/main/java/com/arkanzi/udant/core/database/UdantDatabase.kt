package com.arkanzi.udant.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.arkanzi.udant.core.database.converters.DownloadJobConverters
import com.arkanzi.udant.core.database.converters.ArchiveStatusConverters
import com.arkanzi.udant.core.database.dao.ArticleDao
import com.arkanzi.udant.core.database.dao.ArticleCollectionDao
import com.arkanzi.udant.core.database.dao.CollectionDao
import com.arkanzi.udant.core.database.dao.DownloadJobDao
import com.arkanzi.udant.core.database.dao.ExtensionDao
import com.arkanzi.udant.core.database.dao.FeedCategoryDao
import com.arkanzi.udant.core.database.dao.SavedArticleDao
import com.arkanzi.udant.core.database.entity.ArticleCollectionCrossRef
import com.arkanzi.udant.core.database.entity.ArticleEntity
import com.arkanzi.udant.core.database.entity.CollectionEntity
import com.arkanzi.udant.core.database.entity.DownloadJobEntity
import com.arkanzi.udant.core.database.entity.ExtensionEntity
import com.arkanzi.udant.core.database.entity.FeedCategoryEntity
import com.arkanzi.udant.core.database.entity.SavedArticleEntity

@Database(
    entities = [
        CollectionEntity::class,
        ArticleCollectionCrossRef::class,
        ArticleEntity::class,
        ExtensionEntity::class,
        FeedCategoryEntity::class,
        SavedArticleEntity::class,
        DownloadJobEntity::class
    ],
    version = 17,
    exportSchema = false
)
@TypeConverters(
    ArchiveStatusConverters::class,
    DownloadJobConverters::class
)
abstract class UdantDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun collectionDao(): CollectionDao
    abstract fun articleCollectionDao(): ArticleCollectionDao
    abstract fun feedCategoryDao(): FeedCategoryDao
    abstract fun extensionDao(): ExtensionDao
    abstract fun savedArticleDao(): SavedArticleDao
    abstract fun downloadJobDao(): DownloadJobDao
}