package com.arkanzi.udant.feature.archive.repository

import android.content.Context
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.arkanzi.udant.core.database.dao.SavedArticleDao
import com.arkanzi.udant.core.model.ArchiveStatus
import com.arkanzi.udant.core.preferences.AppPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveRepository@Inject constructor(
    private val savedArticleDao: SavedArticleDao,
    private val appPreferencesRepository: AppPreferencesRepository,
    @ApplicationContext
    private val context: Context
) {
    fun getArchiveFolderUri(): Flow<String?> {

        return appPreferencesRepository
            .getArchiveFolderUri()
    }

    suspend fun setQueued(
        savedArticleId: Long
    ) {

        savedArticleDao.updateArchiveStatus(
            savedArticleId = savedArticleId,
            archiveStatus = ArchiveStatus.QUEUED
        )
    }

    suspend fun setArchiving(
        savedArticleId: Long
    ) {

        savedArticleDao.updateArchiveStatus(
            savedArticleId = savedArticleId,
            archiveStatus = ArchiveStatus.ARCHIVING
        )
    }

    suspend fun setFailed(
        savedArticleId: Long
    ) {

        savedArticleDao.updateArchiveStatus(
            savedArticleId = savedArticleId,
            archiveStatus = ArchiveStatus.FAILED
        )
    }

    suspend fun setCompleted(
        savedArticleId: Long,
        archiveUri: String
    ) {

        savedArticleDao.updateArchiveCompleted(
            savedArticleId = savedArticleId,
            archiveUri = archiveUri
        )
    }

    suspend fun deleteArchive(
        savedArticleId: Long
    ){

        val article =
            savedArticleDao
                .getSavedArticleById(
                    savedArticleId
                ) ?: return

        val archiveUri =
            article.archiveUri
                ?: return

        val documentFile =
            DocumentFile.fromSingleUri(
                context,
                archiveUri.toUri()
            ) ?: return

        if (!documentFile.exists()) {
            savedArticleDao.clearArchive(
                savedArticleId
            )
            return
        }

        if (
            documentFile.delete()
        ) {
            savedArticleDao.clearArchive(
                savedArticleId
            )
        }
    }

    suspend fun getSavedArticleById(
        savedArticleId: Long
    ) = savedArticleDao.getSavedArticleById(
        savedArticleId = savedArticleId
    )
}