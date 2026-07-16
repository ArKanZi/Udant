package com.arkanzi.udant.core.job.dispatcher

import com.arkanzi.udant.core.job.event.Events
import com.arkanzi.udant.core.job.model.ProgressState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadDispatcher @Inject constructor() {
    private val _progressState = MutableSharedFlow<ProgressState>()
    private val _events = MutableSharedFlow<Events>(replay = 0, extraBufferCapacity = 1)

    val progressState: SharedFlow<ProgressState> = _progressState

    val events: SharedFlow<Events> = _events

    suspend fun emitProgress(progress: ProgressState){
        _progressState.emit(progress)
    }
    suspend fun emitEvent(event: Events){
        _events.emit(event)
    }
}