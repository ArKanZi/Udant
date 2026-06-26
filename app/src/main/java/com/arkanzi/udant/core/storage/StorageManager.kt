package com.arkanzi.udant.core.storage

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageManager @Inject constructor(
    @param:ApplicationContext
    private val context: Context
) {
    private fun getDocumentFile(fileUri: Uri?): DocumentFile? =
        fileUri?.let { DocumentFile.fromSingleUri(context, it) }

    private fun getFolderFile(folderUri: Uri?): DocumentFile? =
        folderUri?.let { DocumentFile.fromTreeUri(context, it) }

    fun getLocalFile(fileName: String): File =
        File(
            context.filesDir,
            fileName
        )

    fun deleteLocalFile(file: File): Boolean = file.delete()

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

    fun deleteFileInSaf(folderUri: Uri?, fileUri: Uri): Boolean {
        val doc = getDocumentFile(fileUri)
        return if (hasPermission(folderUri) && doc?.exists() == true) {
            doc.delete()
        } else {
            false
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
}