package com.arkanzi.udant.core.job.download.dispatcher

import com.arkanzi.udant.core.job.download.event.DownloadEvents
import com.arkanzi.udant.core.job.download.model.DownloadProgressState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadDispatcher @Inject constructor() {
    private val _downloadProgressState = MutableSharedFlow<DownloadProgressState>()
    private val _downloadEvents = MutableSharedFlow<DownloadEvents>(replay = 0, extraBufferCapacity = 1)

    val downloadProgressState: SharedFlow<DownloadProgressState> = _downloadProgressState

    val downloadEvents: SharedFlow<DownloadEvents> = _downloadEvents

    suspend fun emitProgress(progress: DownloadProgressState){
        _downloadProgressState.emit(progress)
    }
    suspend fun emitEvent(event: DownloadEvents){
        _downloadEvents.emit(event)
    }
}