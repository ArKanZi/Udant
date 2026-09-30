package com.arkanzi.udant.feature.library.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkanzi.udant.core.model.CollectionModel
import com.arkanzi.udant.core.preference.AppPreferenceRepository
import com.arkanzi.udant.feature.library.usecase.collection.CollectionApplicationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds


@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val collectionApplicationService : CollectionApplicationService,
    private val appPreferenceRepository: AppPreferenceRepository
) : ViewModel() {

    val collections = collectionApplicationService.getCollections()

    val defaultArticleCount = collectionApplicationService.getDefaultArticleCount()

    val defaultSaveCollectionId =
        appPreferenceRepository.getDefaultSaveCollectionId()

    fun setDefaultSaveCollection(id: String?) {
        viewModelScope.launch {
            appPreferenceRepository.saveDefaultSaveCollectionId(id)
        }
    }
    private var nameCheckJob: Job? = null

    private val _collectionNameExists = MutableStateFlow(false)
    val collectionNameExists = _collectionNameExists.asStateFlow()

    fun onCollectionNameChanged(name: String) {
        nameCheckJob?.cancel()

        nameCheckJob = viewModelScope.launch {
            delay(500.milliseconds)

            _collectionNameExists.value =
                collectionApplicationService.collectionExists(name)

        }
    }

    fun resetCollectionNameCheck() {
        nameCheckJob?.cancel()
        _collectionNameExists.value = false
    }

    fun createCollection(name: String) {
        viewModelScope.launch {
            collectionApplicationService.createCollection(name)
        }
    }

    fun updateNameCollection(name:String, collectionModel: CollectionModel){
        viewModelScope.launch {
            collectionApplicationService.updateCollectionName(name,collectionModel)
        }
    }

    fun updatePinCollection(isPinned: Boolean, collectionModel: CollectionModel){
        viewModelScope.launch {
            collectionApplicationService.updateCollectionPinned(isPinned = isPinned,collectionModel)
        }
    }

    fun updateDefaultCollection(collectionModelId: String?){
        viewModelScope.launch {
            setDefaultSaveCollection(collectionModelId)
        }
    }


    fun deleteCollection(collectionModel: CollectionModel){
        viewModelScope.launch {
            val result = collectionApplicationService.deleteCollection(collectionModel)
            if (result) {
                setDefaultSaveCollection(null)
            }

        }
    }
}