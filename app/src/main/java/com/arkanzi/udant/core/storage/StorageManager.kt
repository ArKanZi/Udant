package com.arkanzi.udant.core.storage

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.arkanzi.udant.core.preferences.AppPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageManager @Inject constructor(
    @param:ApplicationContext
    private val context: Context,

    private val appPreferencesRepository: AppPreferencesRepository
) {
    private suspend fun getSafFolderUri(): String? {
        return appPreferencesRepository
            .getArchiveFolderUri()
            .firstOrNull()
    }

    private fun getDocumentFile(fileUri: Uri?): DocumentFile? =
        fileUri?.let { DocumentFile.fromSingleUri(context, it) }

    private fun getFolderFile(folderUri: Uri?): DocumentFile? =
        folderUri?.let { DocumentFile.fromTreeUri(context, it) }

    fun getLocalFile(fileName: String): File =
        File(context.filesDir, fileName)

    fun getTempArchiveFile(jobId: String): File =
        getLocalFile(ARCHIVE_TEMP_FILE.format(jobId))

    fun createFileInSaf(folderUri: Uri?, fileName: String, mimeType: String): DocumentFile? {
        val folder = getFolderFile(folderUri) ?: return null

        if (!folder.exists()) {
            return null
        }

        return folder.createFile(
            mimeType,
            fileName
        )
    }

    fun deleteLocalFile(file: File): Boolean = file.delete()

    fun deleteFileInSaf(folderUri: Uri?, fileUri: Uri): Boolean {
        val doc = getDocumentFile(fileUri)
        return if (hasPermission(folderUri) && doc?.exists() == true) {
            doc.delete()
        } else {
            false
        }
    }

    suspend fun moveArchiveToSaf(
        jobId: String,
        articleTitle: String
    ): String? {

        val folderUri = getSafFolderUri() ?: throw IllegalStateException("Archive folder not configured")

        val tempFile = getTempArchiveFile(jobId)

        val safFile = createFileInSaf(
            folderUri = folderUri.toUri(),
            fileName = articleTitle,
            mimeType = "message/rfc822"
        ) ?: throw IllegalStateException("Failed to Create File in SAF")

        return try {
            copyToSaf(
                tempFile,
                safFile.uri
            )
            if (!deleteLocalFile(tempFile)) {
                Log.w(
                    TAG,
                    "Failed to delete temporary archive: $tempFile"
                )
            }

            safFile.uri.toString()

        } catch (_: Exception) {

            throw IllegalStateException("Failed to Move File in SAF")

        }
    }

    fun copyToSaf(localFile: File, destinationUri: Uri): Boolean {
        if (!localFile.exists()) {
            return false
        }
        try {

            val outputStream =
                context.contentResolver
                    .openOutputStream(destinationUri)
                    ?: return false

            localFile.inputStream().use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            return true
        }catch (_: Exception){
            return false
        }
    }

    fun hasPermission(folderUri: Uri?): Boolean {
        return context
            .contentResolver
            .persistedUriPermissions
            .any {
                it.uri == folderUri &&
                        it.isReadPermission &&
                        it.isWritePermission
            }
    }
    companion object {

        private const val TAG =
            "StorageManager"

    }
}

private const val ARCHIVE_TEMP_FILE = "archive_%s.temp"

