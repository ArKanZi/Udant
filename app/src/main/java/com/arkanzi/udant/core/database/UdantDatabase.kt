package com.arkanzi.udant.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.arkanzi.udant.core.database.converters.ArchiveStatusConverter
import com.arkanzi.udant.core.database.dao.ArticleDao
import com.arkanzi.udant.core.database.dao.SavedArticleDao
import com.arkanzi.udant.core.database.entity.ArticleEntity
import com.arkanzi.udant.core.database.entity.SavedArticleEntity

@Database(
    entities = [
        ArticleEntity::class,
        SavedArticleEntity::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(
    ArchiveStatusConverter::class
)
abstract class UdantDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun savedArticleDao(): SavedArticleDao
}