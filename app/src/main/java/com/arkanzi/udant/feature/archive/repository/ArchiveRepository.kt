package com.arkanzi.udant.feature.archive.repository

import com.arkanzi.udant.core.database.dao.SavedArticleDao
import com.arkanzi.udant.core.model.ArchiveStatus
import com.arkanzi.udant.core.preferences.AppPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveRepository@Inject constructor(
    private val savedArticleDao: SavedArticleDao,
    private val appPreferencesRepository: AppPreferencesRepository,

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
            savedArticleDao.clearArchive(
                savedArticleId
            )
    }

    suspend fun getSavedArticleById(
        savedArticleId: Long
    ) = savedArticleDao.getSavedArticleById(
        savedArticleId = savedArticleId
    )
}