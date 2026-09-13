package com.arkanzi.udant.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.arkanzi.udant.core.database.converters.DownloadJobConverters
import com.arkanzi.udant.core.database.converters.ArchiveStatusConverters
import com.arkanzi.udant.core.database.dao.ArticleDao
import com.arkanzi.udant.core.database.dao.DownloadJobDao
import com.arkanzi.udant.core.database.dao.FeedCategoryDao
import com.arkanzi.udant.core.database.dao.SavedArticleDao
import com.arkanzi.udant.core.database.entity.ArticleEntity
import com.arkanzi.udant.core.database.entity.DownloadJobEntity
import com.arkanzi.udant.core.database.entity.FeedCategoryEntity
import com.arkanzi.udant.core.database.entity.SavedArticleEntity

@Database(
    entities = [
        ArticleEntity::class,
        FeedCategoryEntity::class,
        SavedArticleEntity::class,
        DownloadJobEntity::class
    ],
    version = 10,
    exportSchema = false
)
@TypeConverters(
    ArchiveStatusConverters::class,
    DownloadJobConverters::class
)
abstract class UdantDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun feedCategoryDao(): FeedCategoryDao
    abstract fun savedArticleDao(): SavedArticleDao
    abstract fun downloadJobDao(): DownloadJobDao
}