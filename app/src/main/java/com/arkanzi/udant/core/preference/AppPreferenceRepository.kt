package com.arkanzi.udant.core.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferenceRepository @Inject constructor(

    private val dataStore: DataStore<Preferences>

) {

    suspend fun saveArchiveFolderUri(
        uri: String
    ) {

        dataStore.edit { preferences ->

            preferences[
                PreferenceKeys.ARCHIVE_FOLDER_URI
            ] = uri
        }
    }

    fun getPreferences(): Flow<AppPreference> {

        return dataStore.data.map { preferences ->

            AppPreference(

                archiveFolderUri =
                    preferences[
                        PreferenceKeys.ARCHIVE_FOLDER_URI
                    ]
            )
        }
    }

    fun getArchiveFolderUri(): Flow<String?> {

        return dataStore.data.map { preferences ->

            preferences[
                PreferenceKeys.ARCHIVE_FOLDER_URI
            ]
        }
    }
}