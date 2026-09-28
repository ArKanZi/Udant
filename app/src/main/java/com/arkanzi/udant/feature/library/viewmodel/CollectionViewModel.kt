package com.arkanzi.udant.feature.library.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkanzi.udant.core.model.Collection
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
    private val collectionApplicationService : CollectionApplicationService
) : ViewModel() {

    val collections = collectionApplicationService.getCollections()
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

    fun editCollection(name:String,collection: Collection){
        viewModelScope.launch {
            collectionApplicationService.editCollection(name,collection)
        }
    }
}