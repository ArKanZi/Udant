package com.arkanzi.udant.core.di

import android.content.Context
import androidx.room.Room
import com.arkanzi.udant.core.database.UdantDatabase
import com.arkanzi.udant.core.database.dao.ArticleDao
import com.arkanzi.udant.core.database.dao.DownloadJobDao
import com.arkanzi.udant.core.database.dao.SavedArticleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideUdantDatabase(
        @ApplicationContext context: Context
    ): UdantDatabase {

        return Room.databaseBuilder(
                context,
                UdantDatabase::class.java,
                "udant_database"
            ).fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideArticleDao(
        database: UdantDatabase
    ): ArticleDao {

        return database.articleDao()
    }

    @Provides
    @Singleton
    fun provideSavedArticleDao(
        database: UdantDatabase
    ): SavedArticleDao {

        return database.savedArticleDao()
    }

    @Provides
    @Singleton
    fun provideDownloadJobDao(
        database: UdantDatabase
    ): DownloadJobDao {

        return database.downloadJobDao()
    }
}