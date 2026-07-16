package com.arkanzi.udant.core.job.observer

import com.arkanzi.udant.core.job.dispatcher.DownloadDispatcher
import com.arkanzi.udant.core.job.model.ProgressState
import com.arkanzi.udant.core.job.notification.DownloadNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadNotificationObserver @Inject constructor(

    private val downloadDispatcher: DownloadDispatcher,
    private val downloadNotification: DownloadNotification

) {

    private val observerScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    var alreadyStarted = false

    fun start() {

        if (alreadyStarted) return

        alreadyStarted = true


        observerScope.launch {

            downloadDispatcher.progressState.collect { progress ->

                when (progress) {

                    is ProgressState.Queued ->
                        downloadNotification.showQueued(
                            notificationId = progress.notificationId
                        )

                    is ProgressState.Loading ->
                        downloadNotification.showProgress(
                            notificationId = progress.notificationId,
                            progress.progress
                        )

                    is ProgressState.Generating ->
                        downloadNotification.showGenerating(
                            notificationId = progress.notificationId
                        )

                    is ProgressState.Moving ->
                        downloadNotification.showMoving(
                            notificationId = progress.notificationId
                        )

                    is ProgressState.Completed ->
                        downloadNotification.showCompleted(
                            notificationId = progress.notificationId
                        )

                    is ProgressState.Failed ->
                        downloadNotification.showFailed(
                            notificationId = progress.notificationId,
                            reason = progress.reason.toString()
                        )
                }
            }
        }
    }
}