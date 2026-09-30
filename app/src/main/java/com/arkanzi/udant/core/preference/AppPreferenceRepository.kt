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

    suspend fun saveDefaultSaveCollectionId(
        collectionId: String?
    ) {
        dataStore.edit { preferences ->
            if (collectionId == null) {
                preferences.remove(
                    PreferenceKeys.DEFAULT_SAVE_COLLECTION_ID
                )
            } else {
                preferences[
                    PreferenceKeys.DEFAULT_SAVE_COLLECTION_ID
                ] = collectionId
            }
        }
    }

    fun getPreferences(): Flow<AppPreference> {

        return dataStore.data.map { preferences ->

            AppPreference(

                archiveFolderUri =
                    preferences[
                        PreferenceKeys.ARCHIVE_FOLDER_URI
                    ],
                defaultSaveCollectionId =
                    preferences[
                        PreferenceKeys.DEFAULT_SAVE_COLLECTION_ID
                    ]
            )
        }
    }

    fun getDefaultSaveCollectionId(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[
                PreferenceKeys.DEFAULT_SAVE_COLLECTION_ID
            ]
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