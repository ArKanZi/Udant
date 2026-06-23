package com.arkanzi.udant.feature.archive.repository

import com.arkanzi.udant.core.database.dao.SavedArticleDao
import com.arkanzi.udant.core.model.ArchiveStatus
import com.arkanzi.udant.core.preferences.AppPreferencesRepository
import com.arkanzi.udant.feature.archive.model.ArchiveUpdate
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

    suspend fun setStatus(update: ArchiveUpdate){
        when (update) {
            is ArchiveUpdate.NotArchived ->
                setNotArchived(update.savedArticleId)

            is ArchiveUpdate.Queued ->
                setQueued(update.savedArticleId)

            is ArchiveUpdate.Archiving ->
                setArchiving(update.savedArticleId)

            is ArchiveUpdate.Failed ->
                setFailed(update.savedArticleId)

            is ArchiveUpdate.Completed ->
                setCompleted(
                    update.savedArticleId,
                    update.archiveUri
                )
        }
    }

    private suspend fun setNotArchived(savedArticleId: Long){
        savedArticleDao.updateArchiveStatus(
            savedArticleId = savedArticleId,
            archiveStatus = ArchiveStatus.NOT_ARCHIVED
        )
    }
    private suspend fun setQueued(savedArticleId: Long) {

        savedArticleDao.updateArchiveStatus(
            savedArticleId = savedArticleId,
            archiveStatus = ArchiveStatus.QUEUED
        )
    }
    private suspend fun setArchiving(savedArticleId: Long) {

        savedArticleDao.updateArchiveStatus(
            savedArticleId = savedArticleId,
            archiveStatus = ArchiveStatus.ARCHIVING
        )
    }
    private suspend fun setFailed(savedArticleId: Long) {

        savedArticleDao.updateArchiveStatus(
            savedArticleId = savedArticleId,
            archiveStatus = ArchiveStatus.FAILED
        )
    }
    private suspend fun setCompleted(savedArticleId: Long, archiveUri: String) {

        savedArticleDao.updateArchiveCompleted(
            savedArticleId = savedArticleId,
            archiveUri = archiveUri
        )
    }

    suspend fun deleteArchive(savedArticleId: Long){
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