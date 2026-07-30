package com.arkanzi.udant.core.job.download.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkanzi.udant.core.job.download.DownloadManager
import com.arkanzi.udant.core.job.download.dispatcher.DownloadDispatcher
import com.arkanzi.udant.core.job.download.model.DownloadAction
import com.arkanzi.udant.core.job.download.model.DownloadProgressState
import com.arkanzi.udant.core.job.download.repository.DownloadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    repository: DownloadRepository,
    private val downloadManager: DownloadManager,
    private val downloadDispatcher: DownloadDispatcher
) : ViewModel() {

    val downloads = repository.getJobs()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    private val _loadingProgress = MutableStateFlow(0)
    val loadingProgress: StateFlow<Int> = _loadingProgress

    init {
        viewModelScope.launch {
            downloadDispatcher.downloadProgressState.collect { state ->
                if (state is DownloadProgressState.Loading) {
                    _loadingProgress.value = state.progress
                }
            }
        }
    }

    val isRunning: StateFlow<Boolean> = downloadManager.isRunning

    fun start(){
        viewModelScope.launch {
        downloadManager.dispatch(DownloadAction.START)
        }
    }

    fun pause(){
        viewModelScope.launch {
            downloadManager.dispatch(DownloadAction.STOP)
        }
    }
}