package com.arkanzi.udant.feature.settings.viewmodel

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkanzi.udant.core.preference.AppPreference
import com.arkanzi.udant.core.preference.AppPreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri

@HiltViewModel
class SettingsViewModel @Inject constructor(

    private val appPreferenceRepository:
    AppPreferenceRepository

) : ViewModel() {


    val preferences =
        appPreferenceRepository
            .getPreferences()
            .stateIn(
                scope = viewModelScope,
                started =SharingStarted.Lazily,
                initialValue = AppPreference()
            )

    fun saveArchiveFolderUri(
        uri: String
    ) {

        viewModelScope.launch {

            appPreferenceRepository
                .saveArchiveFolderUri(
                    uri = uri
                )
        }
    }

    fun validateArchiveFolder(
        context: Context,
        uriString: String
    ): Boolean {

        return try {

            val uri =
                uriString.toUri()

            val hasPermission =
                context.contentResolver
                    .persistedUriPermissions
                    .any {
                        it.uri == uri
                    }

            val folder =
                DocumentFile.fromTreeUri(
                    context,
                    uri
                )

            val folderExists =
                folder?.exists() == true

            hasPermission &&
                    folderExists

        } catch (_: Exception) {

            false
        }
    }
}